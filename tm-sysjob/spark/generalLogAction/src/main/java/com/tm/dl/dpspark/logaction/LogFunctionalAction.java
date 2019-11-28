package com.tm.dl.dpspark.logaction;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.hbase.DPHbase;
import com.tm.dl.javasdk.dpspark.sqlserver.DPSqlServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 将新表 sys_functional_operation_log数据转存到mssql表sys_tm_intodashboard_log
 * Copyright: © 2017 FanLei. All rights reserved.
 * Company: NULL
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/7/16
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class LogFunctionalAction extends DPSparkBase implements Serializable {
    private final String SOURCE_TABLE_NAME = "sys_functional_operation_log20190722171037";//qa
    private final String MSSQL_URL = "jdbc:sqlserver://10.60.136.172:1433;databaseName=HGS_DW;";//qa
    private final String MSSQL_USERNAME = "HGS_DW";
    private final String MSSQL_PASSWORD = "Foxconn1@#";
//    private final String SOURCE_TABLE_NAME = "sys_functional_operation_log20190715123020";//pro --20190724
//    private final String MSSQL_URL = "jdbc:sqlserver://10.134.224.56:3000;databaseName=HGS_DW;";//pro
//    private final String MSSQL_USERNAME = "HGS_user";
//    private final String MSSQL_PASSWORD = "Foxconn@2016";

    private final String MSSQL_TABLE_NAME = "sys_tm_intodashboard_log";

    @Override
    public void main(Map<String, String> map) throws Exception {
        //每天00:00触发，取前一天的数据
        Scan scan = new Scan();

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String beginDate = String.valueOf(calendar.getTimeInMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        String endDate = String.valueOf(calendar.getTimeInMillis());

        JavaPairRDD<ImmutableBytesWritable, Result> functionalLogResultRdd = DPHbase.saltRddRead(SOURCE_TABLE_NAME, beginDate, endDate, scan);
//        System.out.println("========get functional operation log total :" + functionalLogResultRdd.count() + "========");
        log.info("========get functional operation log total :" + functionalLogResultRdd.count() + "========");
        JavaRDD<Row> functionalLogRdd = functionalLogResultRdd.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Row>() {
            @Override
            public Row call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                Result result = tuple2._2();
                byte[] bloguser = result.getValue("info".getBytes(), "loguser".getBytes());
                byte[] bfunctionid = result.getValue("info".getBytes(), "functionid".getBytes());
                byte[] bfunctionname = result.getValue("info".getBytes(), "functionname".getBytes());
                byte[] beventtime = result.getValue("info".getBytes(), "eventtime".getBytes());
                byte[] bclienttype = result.getValue("info".getBytes(), "clienttype".getBytes());
                byte[] bsysname = result.getValue("info".getBytes(), "sysname".getBytes());
                String sysname = bsysname == null ? "" : new String(bsysname);
                String eventtime = beventtime == null ? "" : new String(beventtime);
                String functionid = bfunctionid == null ? "" : new String(bfunctionid);
                String functionname = bfunctionname == null ? "" : new String(bfunctionname);
                String clienttype = bclienttype == null ? "" : new String(bclienttype);
                String loguser = bloguser == null ? "" : new String(bloguser);
                return RowFactory.create(loguser,functionid,functionname,eventtime,clienttype,sysname);
            }
        });
        //构建dataset
        List<StructField> tableFields = new ArrayList<>();
        tableFields.add(DataTypes.createStructField("usercode", DataTypes.StringType, true));
        tableFields.add(DataTypes.createStructField("menuid", DataTypes.StringType, true));
        tableFields.add(DataTypes.createStructField("menuname", DataTypes.StringType, true));
        tableFields.add(DataTypes.createStructField("systimestamp", DataTypes.StringType, true));
        tableFields.add(DataTypes.createStructField("terminaltype", DataTypes.StringType, true));
        tableFields.add(DataTypes.createStructField("sysname", DataTypes.StringType, true));

        StructType dsSchema = DataTypes.createStructType(tableFields);
        Dataset<Row> ds = DPSparkApp.getSession().createDataFrame(functionalLogRdd, dsSchema);
        ds.createOrReplaceTempView("tm_functional_operation_log");
        //排序一下
        Dataset<Row> functionalLogDs = DPSparkApp.getSession().sql("select usercode,menuid,menuname,from_unixtime(systimestamp/1000) as dtime,terminaltype,sysname from tm_functional_operation_log order by dtime DESC");
        functionalLogDs.show();

        //构建mssql的schema
        HashMap<String, StructField> fieldHashMap = new HashMap<>();
        fieldHashMap.putIfAbsent("usercode", DataTypes.createStructField("usercode", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("menuid", DataTypes.createStructField("menuid", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("menuname", DataTypes.createStructField("menuname", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("dtime", DataTypes.createStructField("dtime", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("terminaltype", DataTypes.createStructField("terminaltype", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("sysname", DataTypes.createStructField("sysname", DataTypes.StringType, true));

        //写入mssql
        DPSqlServer.commonOdbcWriteBatch(MSSQL_URL,
                MSSQL_USERNAME, MSSQL_PASSWORD, MSSQL_TABLE_NAME, functionalLogDs.toJavaRDD(), fieldHashMap, functionalLogDs.schema());

        DPSparkApp.stop();

    }

}
