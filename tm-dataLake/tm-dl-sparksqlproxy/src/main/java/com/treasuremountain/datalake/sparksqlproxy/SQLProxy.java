package com.treasuremountain.datalake.sparksqlproxy;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.DPSaltTableInputFormat;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLHbOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseTableMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import com.twitter.chill.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.primitives.Doubles;
import org.apache.hbase.thirdparty.com.google.common.primitives.Longs;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.*;
import org.apache.spark.util.CollectionAccumulator;
import org.bouncycastle.crypto.ec.ECElGamalDecryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLProxy {

    public static CollectionAccumulator<Channel> channel;
    public static CollectionAccumulator<List<TableSchemaEntity>> tses;
    private static Gson gson = new Gson();
    private static Logger logger = LoggerFactory.getLogger(SQLProxy.class);

    public static void main(String[] args) throws IOException, TimeoutException {

        StringBuilder argsb = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            argsb.append(args[i]);
            argsb.append(" ");
        }

        String arg = argsb.toString();

        System.out.println("parm:" + arg);

        SparkSqlParamsDto parms = gson.fromJson(arg, SparkSqlParamsDto.class);

        String quorum = "zkQuorum1,zkQuorum2,zkQuorum3";
        String clientPort = "2181";

        // 部署
        SparkSession spark = SparkSession.builder()
                .appName("SQLProxy")
                .master("yarn")
                .getOrCreate();

        // 开发测试 add ref.tian
        /*SparkSession spark = SparkSession.builder()
                .appName("SQLProxy")
                .master("local[*]")
                .config("spark.executor.memory", "2g")
                .config("spark.driver.memory", "4g")
                .getOrCreate();*/

        SparkContext context = spark.sparkContext();

        JavaSparkContext jcontext = new JavaSparkContext(context);

        TMDLHbOperator.init(quorum, clientPort);

        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", quorum);
        configuration.set("hbase.zookeeper.clientPort", clientPort);

        logger.info("====开始进行数据查询，数据会输出到：" + parms.getMqName());

        List<TableSchemaEntity> schemaEntityList = new ArrayList<>();

        TMRabbitMqOperator.connect("rabbitmq1", "admin", "admin", 5672, 9);

        try {
            parms.getTables().stream().forEach(d -> {
                try {
                    String tableName = d.getTableName();
                    String startRowKey = d.getStartRowKey();
                    String endRowKey = d.getEndRowKey();

                    HbaseTableMsg tableInfo;

                    if (d.getIsSalt().equals("true")) {
                        tableInfo = TMDLHbOperator.querySaltTable(tableName, null, startRowKey, endRowKey, 1);
                    } else {
                        tableInfo = TMDLHbOperator.queryTable(tableName, null, "0", "z", 1);
                    }

                    List<StructField> structFields = new ArrayList<StructField>();

                    structFields.add(DataTypes.createStructField("id", DataTypes.StringType, true));
                    ConcurrentHashMap<String, List<StructField>> structFamilyFields = new ConcurrentHashMap<>();
                    tableInfo.getRowList().get(0).getColumnFamily().stream().forEach(e -> {
                        String family = e.getColumnFamilyName();
                        e.getColumnList().stream().forEach(f -> {
                            String colum = f.getColumnName();
                            String value = f.getColumnValue();
                            DataType type;
                            Long lv = Longs.tryParse(value);
                            Double dv = Doubles.tryParse(value);
                            if (lv != null) {
                                type = DataTypes.LongType;
                            } else if (dv != null) {
                                type = DataTypes.DoubleType;
                            } else {
                                type = DataTypes.StringType;
                            }
//                            StructField temp = DataTypes.createStructField(String.format("%s_%s", family, colum), type, true);
                            structFields.add(DataTypes.createStructField(String.format("%s_%s", family, colum), type, true));
                            List<StructField> structFieldstemp = structFamilyFields.get(family);
                            if (structFieldstemp != null) {
                                structFieldstemp.add(DataTypes.createStructField(colum, type, true));
                                structFamilyFields.replace(family, structFieldstemp);
                            } else {
                                structFieldstemp = new ArrayList<>();
                                structFieldstemp.add(DataTypes.createStructField(colum, type, true));
                                structFamilyFields.put(family, structFieldstemp);
                            }
                        });
                    });

                    TableSchemaEntity te = new TableSchemaEntity();
                    te.setTableName(tableName);
                    te.setStructFields(structFields);

                    te.setStructFamilyFields(structFamilyFields);
                    schemaEntityList.add(te);
                } catch (Exception ex) {
                    System.out.println(ex);
                    try {
                        TMRabbitMqOperator.publishExchange(parms.getMqName(), "===is end ===");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            if (!schemaEntityList.isEmpty()) {
                final Broadcast<List<TableSchemaEntity>> tseBroadcast = jcontext.broadcast(schemaEntityList);
                parms.getTables().stream().forEach(d -> {
                    String tableName = d.getTableName();
                    configuration.set(TableInputFormat.INPUT_TABLE, tableName);
                    String startRowKey = d.getStartRowKey();
                    String endRowKey = d.getEndRowKey();
                    JavaPairRDD<ImmutableBytesWritable, Result> myRDD = null;
                    if (d.getIsSalt().equals("true")) {
                        configuration.set(TableInputFormat.SCAN_ROW_START, startRowKey);
                        configuration.set(TableInputFormat.SCAN_ROW_STOP, endRowKey);
                        myRDD = jcontext.newAPIHadoopRDD(configuration, DPSaltTableInputFormat.class, ImmutableBytesWritable.class, Result.class);
                    } else {
                        Scan scan = new Scan();
                        if (StringUtils.isNotBlank(startRowKey)) {
                            scan.withStartRow(Bytes.toBytes(startRowKey));
                        }
                        if (StringUtils.isNotBlank(endRowKey)) {
                            scan.withStopRow(Bytes.toBytes(endRowKey));
                        }
                        ClientProtos.Scan proto = null;

                        try {
                            proto = ProtobufUtil.toScan(scan);
                        } catch (IOException e) {
                            try {
                                TMRabbitMqOperator.publishExchange(parms.getMqName(), "===is end ===");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        String ScanToString = Base64.encodeBytes(proto.toByteArray());
                        configuration.set(TableInputFormat.SCAN, ScanToString);
                        myRDD = jcontext.newAPIHadoopRDD(configuration, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);
                    }

                    JavaRDD<Row> personsRDD = myRDD.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Row>() {
                        @Override
                        public Row call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {

                            TableSchemaEntity rtse = tseBroadcast.value().stream().filter(f -> f.getTableName().equals(tableName)).findFirst().get();
                            Result result = tuple._2();
                            int cellsCount = rtse.getStructFields().size() + 1;
                            Object[] rows = new Object[cellsCount];
                            String rowkey = Bytes.toString(result.getRow());
                            rows[0] = rowkey;
                            AtomicInteger i = new AtomicInteger(1);
                            rtse.getStructFamilyFields().forEach((k, v) -> {
                                v.forEach(p -> {
                                    byte[] tempvalue = result.getValue(k.getBytes(), p.name().getBytes());
                                    if (tempvalue != null && tempvalue.length > 0) {
                                        DataType type = p.dataType();
                                        if (type == DataTypes.LongType) {
                                            Long lv = Longs.tryParse(Bytes.toString(tempvalue));
                                            if (lv != null) {
                                                rows[i.getAndIncrement()] = lv;
                                            } else {
                                                rows[i.getAndIncrement()] = 0L;
                                                logger.error("数据转换异常：" + p.name() + "=====" + Bytes.toString(tempvalue));
                                            }
                                        } else if (type == DataTypes.DoubleType) {
                                            Double dv = Doubles.tryParse(Bytes.toString(tempvalue));
                                            if (dv != null) {
                                                rows[i.getAndIncrement()] = dv;
                                            } else {
                                                rows[i.getAndIncrement()] = 0.0d;
                                                logger.error("数据转换异常：" + p.name() + "=====" + Bytes.toString(tempvalue));
                                            }
                                        } else {
                                            rows[i.getAndIncrement()] = Bytes.toString(tempvalue);
                                        }
                                    } else {
                                        DataType type = p.dataType();
                                        if (type == DataTypes.LongType) {
                                            rows[i.getAndIncrement()] = 0L;
                                        } else if (type == DataTypes.DoubleType) {
                                            rows[i.getAndIncrement()] = 0.0d;
                                        } else {
                                            rows[i.getAndIncrement()] = "";
                                        }
                                    }
                                });
                            });
                            return RowFactory.create(rows);
                        }
                    });
                    List<StructField> structFields = schemaEntityList.stream()
                            .filter(f -> f.getTableName().equals(tableName)).findFirst().get().getStructFields();
                    StructType schema = DataTypes.createStructType(structFields);
                    Dataset stuDf = spark.createDataFrame(personsRDD, schema);
                    stuDf.printSchema();
                    stuDf.createOrReplaceTempView(tableName);
                });
                Dataset<Row> nameDf = spark.sql(parms.getSql());

                CollectionAccumulator<String> rjson = context.collectionAccumulator("result");

                nameDf.cache().toJSON().foreach(d -> {
                    logger.info("====cahe.foreach============" + d);
                    rjson.add(d);
                });

                rjson.value().stream().forEach(d -> {
                    try {
                        TMRabbitMqOperator.publishExchange(parms.getMqName(), d);
                        logger.info("====数据已经输出到：" + parms.getMqName() + "===" + d);
                    } catch (IOException e) {
                        System.out.println(e);
                        logger.error("发送mq出现异常：" + e.getMessage(), e);
                    }
                });
                TMRabbitMqOperator.publishExchange(parms.getMqName(), "===is end ===");
            } else {
                TMRabbitMqOperator.publishExchange(parms.getMqName(), "===is end ===");
            }
        } catch (Exception e) {
            TMRabbitMqOperator.publishExchange(parms.getMqName(), "===is end ===");
            logger.error("===========error====" + e.getMessage(), e);
        }

        spark.close();
    }
}