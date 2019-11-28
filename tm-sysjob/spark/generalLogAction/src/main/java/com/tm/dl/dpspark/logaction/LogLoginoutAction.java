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
 * Description: 将新表 sys_user_loginout_log数据转存到mssql表 sys_tm_login_log
 * Copyright: © 2017 FanLei. All rights reserved.
 * Company: NULL
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/7/16
 */
@Slf4j
public class LogLoginoutAction extends DPSparkBase implements Serializable {
    //qa
    private final String SOURCE_TABLE_NAME = "sys_user_loginout_log20190722165832";
    private final String MSSQL_URL = "jdbc:sqlserver://10.60.136.172:1433;databaseName=HGS_DW;";
    private final String MSSQL_USERNAME = "HGS_DW";
    private final String MSSQL_PASSWORD = "Foxconn1@#";
    //pro
//    private final String SOURCE_TABLE_NAME = "sys_user_loginout_log20190715122419";
//    private final String MSSQL_URL = "jdbc:sqlserver://10.134.224.56:3000;databaseName=HGS_DW;";//pro
//    private final String MSSQL_USERNAME = "HGS_user";
//    private final String MSSQL_PASSWORD = "Foxconn@2016";

    private final String MSSQL_TABLE_NAME = "sys_tm_login_log";

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

        JavaPairRDD<ImmutableBytesWritable, Result> loginoutLogResultRdd = DPHbase.saltRddRead(SOURCE_TABLE_NAME, beginDate, endDate, scan).filter(new Function<Tuple2<ImmutableBytesWritable, Result>, Boolean>() {
            @Override
            public Boolean call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                Result result = tuple2._2();
                //筛选LOGIN的数据，去掉LOGOUT
                String type = new String(result.getValue("info".getBytes(), "logtype".getBytes()));
                if ("LOGIN".equalsIgnoreCase(type)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        log.info("========get loginout log total :" + loginoutLogResultRdd.count() + "========");
        JavaRDD<Row> loginoutLogRdd = loginoutLogResultRdd.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Row>() {
            @Override
            public Row call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                Result result = tuple2._2();
                byte[] bsysname = result.getValue("info".getBytes(), "sysname".getBytes());
                byte[] beventtime = result.getValue("info".getBytes(), "eventtime".getBytes());
                byte[] bclienttype = result.getValue("info".getBytes(), "clienttype".getBytes());
                byte[] bloguser = result.getValue("info".getBytes(), "loguser".getBytes());
                String sysname = bsysname == null ? "" : new String(bsysname);
                String eventtime = beventtime == null ? "" : new String(beventtime);

                String clienttype = bclienttype == null ? "" : new String(bclienttype);
                String loguser = bloguser == null ? "" : new String(bloguser);
                return RowFactory.create(loguser, eventtime, clienttype, sysname);
            }
        });
        List<StructField> tableFields = new ArrayList<>();
        tableFields.add(DataTypes.createStructField("usercode", DataTypes.StringType, true));
        tableFields.add(DataTypes.createStructField("systimestamp", DataTypes.StringType, true));
        tableFields.add(DataTypes.createStructField("terminaltype", DataTypes.StringType, true));
        tableFields.add(DataTypes.createStructField("sysname", DataTypes.StringType, true));

        StructType dsSchema = DataTypes.createStructType(tableFields);
        Dataset<Row> ds = DPSparkApp.getSession().createDataFrame(loginoutLogRdd, dsSchema);
        ds.createOrReplaceTempView("tm_loginout_log");
        //排序一下
        Dataset<Row> inoutLogDs = DPSparkApp.getSession().sql("select usercode,from_unixtime(systimestamp/1000) as dtime,terminaltype,sysname from tm_loginout_log order by dtime DESC");
        inoutLogDs.show();

        //构建mssql的schema
        HashMap<String, StructField> fieldHashMap = new HashMap<>();
        fieldHashMap.putIfAbsent("usercode", DataTypes.createStructField("usercode", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("dtime", DataTypes.createStructField("dtime", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("terminaltype", DataTypes.createStructField("terminaltype", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("sysname", DataTypes.createStructField("sysname", DataTypes.StringType, true));

        //写入mssql
        DPSqlServer.commonOdbcWriteBatch(MSSQL_URL,
                MSSQL_USERNAME, MSSQL_PASSWORD, MSSQL_TABLE_NAME, inoutLogDs.toJavaRDD(), fieldHashMap, inoutLogDs.schema());
        DPSparkApp.stop();

    }

}
