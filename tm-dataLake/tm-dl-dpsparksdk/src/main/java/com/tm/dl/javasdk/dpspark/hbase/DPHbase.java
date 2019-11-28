package com.tm.dl.javasdk.dpspark.hbase;

import com.alibaba.fastjson.JSONObject;
import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.PermissionManager;
import com.tm.dl.javasdk.dpspark.es.DPEs;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSourceBatchMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.DPSaltTableInputFormat;
import com.twitter.chill.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.spark.JavaHBaseContext;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DPHbase {

    public static JavaPairRDD<ImmutableBytesWritable, Result> rddRead(String tableName, Scan scan) throws IOException {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        JavaSparkContext context = DPSparkApp.getContext();
        //初始化Hbase连接参数
        Configuration configuration = pm.initialHbaseSecurityContext();
        JavaHBaseContext hBaseContext = new JavaHBaseContext(context, configuration);
        JavaRDD<Tuple2<ImmutableBytesWritable, Result>> myrdd = hBaseContext.hbaseRDD(TableName.valueOf(tableName), scan, null);
        JavaPairRDD result = myrdd.mapToPair((PairFunction<Tuple2<ImmutableBytesWritable, Result>, Object, Object>) tuple2 -> new Tuple2<>(tuple2._1(), tuple2._2()));
        return result;

    }

    public static JavaPairRDD<ImmutableBytesWritable, Result> saltRddRead(String tableName, String startRowKey, String endRowKey, Scan scan) throws IOException {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        JavaSparkContext context = DPSparkApp.getContext();
        //初始化Hbase连接参数
        Configuration configuration = pm.initialHbaseSecurityContext();
        configuration.set(TableInputFormat.INPUT_TABLE, tableName);
        configuration.set(TableInputFormat.SCAN_ROW_START, startRowKey);
        configuration.set(TableInputFormat.SCAN_ROW_STOP, endRowKey);

        ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
        String ScanToString = Base64.encodeBytes(proto.toByteArray());
        configuration.set(TableInputFormat.SCAN, ScanToString);
        //特殊加盐读取只能使用DPSaltTableInputFormat.class
        JavaPairRDD<ImmutableBytesWritable, Result> myRDD = context.newAPIHadoopRDD(configuration, DPSaltTableInputFormat.class, ImmutableBytesWritable.class, Result.class);

        return myRDD;
    }

    //    public static void rddWrite(String tableName, JavaPairRDD<ImmutableBytesWritable, Put> puts) throws IOException {
//        PermissionManager pm = DPSparkApp.getDpPermissionManager();
//        Configuration configuration = pm.initialHbaseSecurityContext();
//
//        Job jobconf = Job.getInstance(configuration);
//        jobconf.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, tableName);
//        jobconf.setOutputFormatClass(org.apache.hadoop.hbase.mapreduce.TableOutputFormat.class);
//
//        puts.saveAsNewAPIHadoopDataset(jobconf.getConfiguration());
//    }

    /**
     * 批量写，传入构造好的Put对象
     *
     * @param tableName
     * @param puts
     * @throws IOException
     */
    public static void rddWrite(String tableName, JavaRDD<Put> puts) throws IOException, InterruptedException {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        Configuration configuration = pm.initialHbaseSecurityContext();
        //获得hbase表对应的index name
        String indexName = pm.getCurrentIndexName(tableName);
        JavaHBaseContext hBaseContext = new JavaHBaseContext(DPSparkApp.getContext(), configuration);
        hBaseContext.bulkPut(puts, TableName.valueOf(tableName), (Function<Put, Put>) put -> put);
        //写入索引
        JavaRDD<MTSourceBatchMsg> indexData = puts.map(new Function<Put, MTSourceBatchMsg>() {
            @Override
            public MTSourceBatchMsg call(Put put) throws Exception {
                MTSourceBatchMsg mtSourceBatchMsg = new MTSourceBatchMsg();
                mtSourceBatchMsg.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                net.sf.json.JSONObject indexBody = new net.sf.json.JSONObject();
                put.getFamilyCellMap().forEach((k, v) -> {
                    v.stream().forEach(cell -> {
                        String rowkey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String columnFamily = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                        String columnQualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                        String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        indexBody.put(columnFamily + "_" + columnQualifier, value);
                        indexBody.put("content", rowkey);//rowkey

                    });
                });
                mtSourceBatchMsg.setBody(indexBody);
                return mtSourceBatchMsg;
            }
        });
        DPEs.addIndexBatch(indexName, indexData.collect());
    }

    /**
     * 批量插入
     *
     * @param tableName The hbase table name
     * @param data      The RDD contains data to be written to Hbase,
     *                  the format of data is {rowkey,columnfamily,qualifier,value}
     *                  e.g, {"1,cf1,col1,2"}
     * @throws IOException
     */
    public static void rddBulkInsert(String tableName, JavaRDD<String> data) throws IOException, InterruptedException {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        JavaSparkContext context = DPSparkApp.getContext();
        Configuration conf = pm.initialHbaseSecurityContext();
        //获得hbase表对应的index name
        String indexName = pm.getCurrentIndexName(tableName);
        JavaHBaseContext hBaseContext = new JavaHBaseContext(context, conf);
        hBaseContext.bulkPut(data, TableName.valueOf(tableName), (Function<String, Put>) s -> {
            String[] cells = s.split(",");
            Put put = new Put(Bytes.toBytes(cells[0]));
            put.addColumn(Bytes.toBytes(cells[1]), Bytes.toBytes(cells[2]), Bytes.toBytes(cells[3]));
            return put;
        });
        //写入索引
        JavaRDD<MTSourceBatchMsg> indexData = data.map((Function<String, MTSourceBatchMsg>) s -> {
            MTSourceBatchMsg mtSourceBatchMsg = new MTSourceBatchMsg();
            mtSourceBatchMsg.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            net.sf.json.JSONObject indexBody = new net.sf.json.JSONObject();
            //构造indexbody, columnFamily_columnName:value
            String[] cells = s.split(",");
            indexBody.put("content", cells[0]);//rowkey
            indexBody.put(cells[1] + "_" + cells[2], cells[3]);
            mtSourceBatchMsg.setBody(indexBody);
            return mtSourceBatchMsg;
        });
        DPEs.addIndexBatch(indexName, indexData.collect());
    }

    /**
     * 根据rowkey批量读取
     *
     * @param rowkeyList
     * @param tableName
     * @return
     * @throws IOException
     */
    public static JavaRDD<Result> rddBulkGetByRowkey(List<byte[]> rowkeyList, String tableName) throws IOException {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        Configuration conf = pm.initialHbaseSecurityContext();
        JavaHBaseContext hBaseContext = new JavaHBaseContext(DPSparkApp.getContext(), conf);
        JavaRDD<byte[]> rddParams = DPSparkApp.getContext().parallelize(rowkeyList);
        return hBaseContext.bulkGet(TableName.valueOf(tableName), 2, rddParams, Get::new, (Function<Result, Result>) result -> result);
    }

    /**
     * 判断rowkey是否存在
     *
     * @param rowkeyList
     * @param tableName
     * @return
     * @throws IOException
     */
    public static ConcurrentHashMap<byte[], Boolean> isExistByRowkeyList(List<byte[]> rowkeyList, String tableName) throws IOException {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        Configuration conf = pm.initialHbaseSecurityContext();
        JavaHBaseContext hBaseContext = new JavaHBaseContext(DPSparkApp.getContext(), conf);
        JavaRDD<byte[]> rddParams = DPSparkApp.getContext().parallelize(rowkeyList);
        JavaRDD<byte[]> resultJavaRDD = hBaseContext.bulkGet(TableName.valueOf(tableName), 2, rddParams, Get::new, (Function<Result, byte[]>) Result::getRow);
        ConcurrentHashMap<byte[], Boolean> result = new ConcurrentHashMap<>();
        List<byte[]> resultList = resultJavaRDD.intersection(rddParams).collect();
        rowkeyList.forEach(s -> {
            result.put(s, false);
            for (byte[] r : resultList) {
                if (Arrays.equals(r, s)) {
                    result.put(r, true);
                }
            }
        });
        return result;
    }


}
