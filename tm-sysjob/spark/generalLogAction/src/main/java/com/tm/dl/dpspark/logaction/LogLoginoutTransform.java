package com.tm.dl.dpspark.logaction;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.hbase.DPHbase;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Description:  将旧表tm_useraction_log中的登陆登出信息转存到sys_user_loginout_log
 * Copyright: © 2019 Maxnerva. All rights reserved.
 * Company: IISD
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/7/22
 */
public class LogLoginoutTransform extends DPSparkBase implements Serializable {
    //qa
    private final String SOURCE_TABLE_NAME = "tm_useraction_log20190722094428";
    private final String LOGIN_TABLE_NAME = "sys_user_loginout_log20190715134818";
    private final String DASHBOARD_TABLE_NAME = "sys_functional_operation_log20190715135032";
    //pro
//    private final String SOURCE_TABLE_NAME = "tm_useraction_log_20190523085246";
//    private final String LOGIN_TABLE_NAME = "sys_user_loginout_log20190715122419";
//    private final String DASHBOARD_TABLE_NAME = "sys_functional_operation_log20190715123020";

    @Override
    public void main(Map<String, String> map) throws Exception {
        Scan scan = new Scan();
        //全表扫描旧表
        JavaPairRDD<ImmutableBytesWritable, Result> oldTableResultRdd = DPHbase.rddRead(SOURCE_TABLE_NAME, scan);
        JavaPairRDD loginoutRdd = oldTableResultRdd.filter(new Function<Tuple2<ImmutableBytesWritable, Result>, Boolean>() {
            @Override
            public Boolean call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                Result result = tuple2._2();
                //整合数据
                String type = new String(result.getValue("info".getBytes(), "type".getBytes()));
                if ("LOGIN".equalsIgnoreCase(type) || "LOGOUT".equalsIgnoreCase(type)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        JavaPairRDD dashboardRdd = oldTableResultRdd.filter(new Function<Tuple2<ImmutableBytesWritable, Result>, Boolean>() {
            @Override
            public Boolean call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                Result result = tuple2._2();
                //整合数据
                String type = new String(result.getValue("info".getBytes(), "type".getBytes()));
                if ("INTODASHBOARD".equalsIgnoreCase(type)) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        JavaPairRDD a = loginoutRdd.mapToPair((PairFunction<Tuple2<ImmutableBytesWritable, Result>, ImmutableBytesWritable, Put>) tuple2 -> {
            Result result = tuple2._2();
            //整合数据
            byte[] btype = result.getValue("info".getBytes(), "type".getBytes());
            byte[] busercode = result.getValue("info".getBytes(), "usercode".getBytes());
            byte[] bsysname = result.getValue("info".getBytes(), "sysname".getBytes());
            byte[] bsystimestamp = result.getValue("info".getBytes(), "systimestamp".getBytes());
            byte[] bclientip = result.getValue("info".getBytes(), "clientip".getBytes());
            byte[] blang = result.getValue("info".getBytes(), "lang".getBytes());
            byte[] bterminaltype = result.getValue("info".getBytes(), "terminaltype".getBytes());
            String type = btype == null ? "" : new String(btype);
            String usercode = busercode == null ? "" : new String(busercode);
            String sysname = bsysname == null ? "" : new String(bsysname);
            String systimestamp = bsystimestamp == null ? "" : new String(bsystimestamp);
            String clientip = bclientip == null ? "" : new String(bclientip);
            String lang = blang == null ? "" : new String(blang);
            String terminaltype = bterminaltype == null ? "" : new String(bterminaltype);

            String uuid = MD5Builder.getMD516String(UUID.randomUUID().toString().replaceAll("-", ""));
            String rowKey = String.format("%02d", RandomUtils.nextInt(1, 11)) + ":" + systimestamp + ":INSIGHT:" + type + ":" + uuid;
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("sysname"), Bytes.toBytes(sysname));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("logtype"), Bytes.toBytes(type));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("eventtime"), Bytes.toBytes(systimestamp));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clientip"), Bytes.toBytes(clientip));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clienttype"), Bytes.toBytes(terminaltype));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clientlang"), Bytes.toBytes(lang));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("loguser"), Bytes.toBytes(usercode));
            return new Tuple2<>(new ImmutableBytesWritable(), put);
        });

        JavaPairRDD b = dashboardRdd.mapToPair((PairFunction<Tuple2<ImmutableBytesWritable, Result>, ImmutableBytesWritable, Put>) tuple2 -> {
            Result result = tuple2._2();
            //整合数据
            byte[] btype = result.getValue("info".getBytes(), "type".getBytes());
            byte[] busercode = result.getValue("info".getBytes(), "usercode".getBytes());
            byte[] bsysname = result.getValue("info".getBytes(), "sysname".getBytes());
            byte[] bsystimestamp = result.getValue("info".getBytes(), "systimestamp".getBytes());
            byte[] bmenuid = result.getValue("info".getBytes(), "menuid".getBytes());
            byte[] bmenuname = result.getValue("info".getBytes(), "menuname".getBytes());
            byte[] bclientip = result.getValue("info".getBytes(), "clientip".getBytes());
            byte[] blang = result.getValue("info".getBytes(), "lang".getBytes());
            byte[] bterminaltype = result.getValue("info".getBytes(), "terminaltype".getBytes());
            String type = btype == null ? "" : new String(btype);
            String usercode = busercode == null ? "" : new String(busercode);
            String sysname = bsysname == null ? "" : new String(bsysname);
            String systimestamp = bsystimestamp == null ? "" : new String(bsystimestamp);
            String menuid = bmenuid == null ? "" : new String(bmenuid);
            String menuname = bmenuname == null ? "" : new String(bmenuname);
            String clientip = bclientip == null ? "" : new String(bclientip);
            String lang = blang == null ? "" : new String(blang);
            String terminaltype = bterminaltype == null ? "" : new String(bterminaltype);
            String uuid = MD5Builder.getMD516String(UUID.randomUUID().toString().replaceAll("-", ""));
            String rowKey = String.format("%02d", RandomUtils.nextInt(1, 11)) + ":" + systimestamp + ":INSIGHT:" + type + ":" + uuid;
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("sysname"), Bytes.toBytes(sysname));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("logtype"), Bytes.toBytes(type));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("eventtime"), Bytes.toBytes(systimestamp));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("functionid"), Bytes.toBytes(menuid));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("functionname"), Bytes.toBytes(menuname));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clientip"), Bytes.toBytes(clientip));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clienttype"), Bytes.toBytes(terminaltype));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clientlang"), Bytes.toBytes(lang));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("loguser"), Bytes.toBytes(usercode));
            return new Tuple2<>(new ImmutableBytesWritable(), put);
        });
        DPHbase.rddWrite(LOGIN_TABLE_NAME, a);
        System.out.println("写入sys_user_loginout表成功");
        DPHbase.rddWrite(DASHBOARD_TABLE_NAME, b);
        System.out.println("写入sys_functional_operation表成功");
        DPSparkApp.stop();
    }
}
