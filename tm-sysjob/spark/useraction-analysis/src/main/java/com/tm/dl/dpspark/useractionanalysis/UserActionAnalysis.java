package com.tm.dl.dpspark.useractionanalysis;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.hbase.DPHbase;
import com.tm.dl.javasdk.dpspark.sqlserver.DPSqlServer;
import org.apache.commons.lang3.RandomUtils;
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
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/5/23.
 * Company: Foxconn
 * Project: TreasureMountain
 */
public class UserActionAnalysis extends DPSparkBase implements Serializable {

    public void main(Map<String, String> map) throws Exception {
        Scan scan = new Scan();
        // 时间范围，值分析前一天的数据
        Calendar calendar = new GregorianCalendar();
        Date cdate=new Date();
        calendar.setTime(cdate);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        String begindate = String.valueOf(calendar.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND,999);
        String enddate = String.valueOf(calendar.getTimeInMillis());

        JavaPairRDD<ImmutableBytesWritable, Result> useractionlogResultRdd = DPHbase.saltRddRead("tm_useraction_log20190722094428", begindate, enddate, scan);
        System.out.println("total:"+useractionlogResultRdd.count());  ;
        JavaRDD<Row> useractionlogRdd = useractionlogResultRdd.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Row>() {
            @Override
            public Row call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                Result resulto = tuple2._2();
                String menuid = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("menuid")));
                String menuname = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("menuname")));
                String systimestamp = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("systimestamp")));
                String timestamp = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("timestamp")));
                String type = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("type")));
                String usercode = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("usercode")));
                String terminaltype = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("terminaltype")));
                return (Row) RowFactory.create(usercode,menuid, menuname, type, systimestamp,terminaltype);
            }
        });
        HashMap<String,StructField> fieldHashMap=new HashMap<>();
        HashMap<String,StructField> loginfieldHashMap=new HashMap<>();
        List<StructField> dashboardfields = new ArrayList<>();

//        System.out.println("数据数量："+useractionlogRdd.count());
        // 处理db 栏位对应关系
        fieldHashMap.putIfAbsent("usercode",DataTypes.createStructField("usercode", DataTypes.StringType,true));
        loginfieldHashMap.putIfAbsent("usercode",DataTypes.createStructField("usercode", DataTypes.StringType,true));
        dashboardfields.add(DataTypes.createStructField("usercode", DataTypes.StringType,true));

        fieldHashMap.putIfAbsent("menuid",DataTypes.createStructField("menuid", DataTypes.StringType,true));
        dashboardfields.add(DataTypes.createStructField("menuid", DataTypes.StringType,true));

        fieldHashMap.putIfAbsent("menuname",DataTypes.createStructField("menuname", DataTypes.StringType,true));
        dashboardfields.add(DataTypes.createStructField("menuname", DataTypes.StringType,true));

        dashboardfields.add(DataTypes.createStructField("type", DataTypes.StringType,true));

        fieldHashMap.putIfAbsent("dtime",DataTypes.createStructField("dtime", DataTypes.StringType,true));
        loginfieldHashMap.putIfAbsent("dtime",DataTypes.createStructField("dtime", DataTypes.StringType,true));

        dashboardfields.add(DataTypes.createStructField("systimestamp", DataTypes.StringType,true));

        fieldHashMap.putIfAbsent("terminaltype",DataTypes.createStructField("terminaltype", DataTypes.StringType,true));
        loginfieldHashMap.putIfAbsent("terminaltype",DataTypes.createStructField("terminaltype", DataTypes.StringType,true));
        dashboardfields.add(DataTypes.createStructField("terminaltype", DataTypes.StringType,true));
        
        StructType dsschema = DataTypes.createStructType(dashboardfields);
        Dataset<Row> ds = DPSparkApp.getSession().createDataFrame(useractionlogRdd,dsschema);

//        ds.show();

        ds.createOrReplaceTempView("tm_useraction_log");

        //处理开启报表的数据
        Dataset<Row> intodashboardDs = DPSparkApp.getSession().sql("select usercode,menuid,menuname,from_unixtime(systimestamp/1000) as dtime,terminaltype " +
                " from tm_useraction_log where type='INTODASHBOARD'  order by systimestamp DESC");
        intodashboardDs.show();

        DPSqlServer.commonOdbcWriteBatch("jdbc:sqlserver://10.134.224.56:3000;databaseName=HGS_DW;",
                "HGS_user","Foxconn@2016","sys_tm_intodashboard_log",intodashboardDs.toJavaRDD(),fieldHashMap,intodashboardDs.schema());


        //处理登录数据
        Dataset<Row> loginDs =DPSparkApp.getSession().sql("select usercode,from_unixtime(systimestamp/1000) as dtime,terminaltype " +
                " from tm_useraction_log where type='LOGIN' " +
                " order by systimestamp DESC ");

//        loginDs.show();

        DPSqlServer.commonOdbcWriteBatch("jdbc:sqlserver://10.134.224.56:3000;databaseName=HGS_DW;",
                "HGS_user","Foxconn@2016","sys_tm_login_log",loginDs.toJavaRDD(),loginfieldHashMap,loginDs.schema());

        DPSparkApp.stop();
    }
}
