package com.iisd.dpsparktest;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.es.DPEs;
import com.tm.dl.javasdk.dpspark.hbase.DPHbase;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSourceBatchMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseColumnEntity;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseColumnFamilyEntity;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseRowEntity;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class writeTwolevelindex extends DPSparkBase implements Serializable {
    public void main(Map<String, String> map) throws Exception {

        Scan scan = new Scan();

//        FilterList filterList = new FilterList();
//        filterList.addFilter(new PageFilter(2));
//
//        scan.setFilter(filterList);

        JavaPairRDD<ImmutableBytesWritable, Result> myRDD = DPHbase.rddRead("mfg_assembly_detail", scan);

        JavaRDD<Row> personsRDD = myRDD.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Row>() {

            public Row call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                Result result = tuple._2();
                String rowkey = Bytes.toString(result.getRow());

                String bu = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("bu")));
                String custpn = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("custpn")));
                String emp_id = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("emp_id")));
                String filename = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("filename")));
                String hhpn = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("hhpn")));
                String id = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("id")));
                String is_disassembly = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("is_disassembly")));
                String keypart_sn = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("keypart_sn")));
                String line_id = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("line_id")));
                String part_name = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("part_name")));
                String scantime = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("scantime")));
                String scantime_stamp = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("scantime_stamp")));
                String station_name = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("station_name")));

                return (Row) RowFactory.create(rowkey, bu, custpn, emp_id, filename, hhpn, id, is_disassembly, keypart_sn, line_id, part_name, scantime, scantime_stamp, station_name);
            }

        });

        personsRDD.foreachPartition(rowIterator -> {

            DPHbase.commonInit();
            DPEs.init();

            int i = 0;
            List<HbaseRowEntity> rowList = new ArrayList<>();
            List<MTSourceBatchMsg> mtSourceBatchMsgList = new ArrayList<>();

            while (rowIterator.hasNext()) {

                Row r = rowIterator.next();

//                String rowkey = r.get(0).toString();
                String bu = r.get(1).toString();
                String custpn = r.get(2).toString();
                String emp_id = r.get(3).toString();
                String filename = r.get(4).toString();
                String hhpn = r.get(5).toString();
                String id = r.get(6).toString();
                String is_disassembly = r.get(7).toString();
                String keypart_sn = r.get(8).toString();
                String line_id = r.get(9).toString();
                String part_name = r.get(10).toString();
                String scantime = r.get(11).toString();
                String scantime_stamp = r.get(12).toString();
                String station_name = r.get(13).toString();

                String rowkey = bu + scantime_stamp + UUID.randomUUID();

                HbaseRowEntity hbaseRowEntity = new HbaseRowEntity();
                hbaseRowEntity.setRowKey(rowkey);

                HbaseColumnFamilyEntity hcfe = new HbaseColumnFamilyEntity();
                hcfe.setColumnFamilyName("mfg_assembly_detail");

                List<HbaseColumnEntity> columnList = new ArrayList<>();

                HbaseColumnEntity bue = new HbaseColumnEntity();
                bue.setColumnName("bu");
                bue.setColumnValue(bu);

                columnList.add(bue);

                HbaseColumnEntity custpne = new HbaseColumnEntity();
                bue.setColumnName("custpn");
                bue.setColumnValue(custpn);

                columnList.add(custpne);

                HbaseColumnEntity emp_ide = new HbaseColumnEntity();
                bue.setColumnName("emp_id");
                bue.setColumnValue(emp_id);

                columnList.add(emp_ide);

                HbaseColumnEntity filenamee = new HbaseColumnEntity();
                bue.setColumnName("filename");
                bue.setColumnValue(filename);

                columnList.add(filenamee);

                HbaseColumnEntity hhpne = new HbaseColumnEntity();
                bue.setColumnName("hhpn");
                bue.setColumnValue(hhpn);

                columnList.add(hhpne);

                HbaseColumnEntity ide = new HbaseColumnEntity();
                bue.setColumnName("id");
                bue.setColumnValue(id);

                columnList.add(ide);

                HbaseColumnEntity is_disassemblye = new HbaseColumnEntity();
                bue.setColumnName("is_disassembly");
                bue.setColumnValue(is_disassembly);

                columnList.add(is_disassemblye);

                HbaseColumnEntity keypart_sne = new HbaseColumnEntity();
                bue.setColumnName("keypart_sn");
                bue.setColumnValue(keypart_sn);

                columnList.add(keypart_sne);

                HbaseColumnEntity line_ide = new HbaseColumnEntity();
                bue.setColumnName("line_id");
                bue.setColumnValue(line_id);

                columnList.add(line_ide);

                HbaseColumnEntity part_namee = new HbaseColumnEntity();
                bue.setColumnName("part_name");
                bue.setColumnValue(part_name);

                columnList.add(part_namee);

                HbaseColumnEntity scantimee = new HbaseColumnEntity();
                bue.setColumnName("scantime");
                bue.setColumnValue(scantime);

                columnList.add(scantimee);

                HbaseColumnEntity scantime_stampe = new HbaseColumnEntity();
                bue.setColumnName("scantime_stamp");
                bue.setColumnValue(scantime_stamp);

                columnList.add(scantime_stampe);

                HbaseColumnEntity station_namee = new HbaseColumnEntity();
                bue.setColumnName("station_name");
                bue.setColumnValue(station_name);

                columnList.add(station_namee);

                hcfe.setColumnList(columnList);
                List<HbaseColumnFamilyEntity> hbaseColumnFamilyEntityList = new ArrayList<>();
                hbaseColumnFamilyEntityList.add(hcfe);
                hbaseRowEntity.setColumnFamily(hbaseColumnFamilyEntityList);

                rowList.add(hbaseRowEntity);

                MTSourceBatchMsg bus = new MTSourceBatchMsg();
                bus.setId(rowkey);

                JSONObject indexBody = new JSONObject();
                indexBody.put("content", rowkey);
                indexBody.put("bu", bu);
                indexBody.put("custpn", custpn);
                indexBody.put("emp_id", emp_id);
                indexBody.put("filename", filename);
                indexBody.put("hhpn", hhpn);
                indexBody.put("id", id);
                indexBody.put("is_disassembly", is_disassembly);
                indexBody.put("keypart_sn", keypart_sn);
                indexBody.put("line_id", line_id);
                indexBody.put("part_name", part_name);
                indexBody.put("scantime", scantime);
                indexBody.put("scantime_stamp", scantime_stamp);
                indexBody.put("station_name", station_name);

                bus.setBody(indexBody);

                mtSourceBatchMsgList.add(bus);

                i++;

                if (i >= 2000) {

//                    DPHbase.commonWrite("mfg_assembly_detail_gerrytest", rowList);
                    DPEs.addIndexBatch("mfg_assembly_detail_gerrytest", mtSourceBatchMsgList);

//                    rowList = new ArrayList<>();
                    mtSourceBatchMsgList = new ArrayList<>();

                    i = 0;
                }
            }

//            DPHbase.commonWrite("mfg_assembly_detail_gerrytest", rowList);
            DPEs.addIndexBatch("mfg_assembly_detail_gerrytest", mtSourceBatchMsgList);

        });

        DPSparkApp.stop();
    }
}
