package com.tm.dl.dpspark.logaction;

import com.alibaba.fastjson.JSONObject;
import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.hbase.DPHbase;
import net.minidev.json.JSONUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.gson.Gson;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

/**
 * Description:  将 tm_functionaloperation_log 表数据转存到新表 sys_operational_details_log
 * Copyright: © 2017 FanLei. All rights reserved.
 * Company: NULL
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/7/16
 */
public class LogOperationDetailTransform extends DPSparkBase implements Serializable {
    //qa
    private final String SOURCE_TABLE_NAME = "tm_functionaloperation_log20190717145740";
    private final String TARGET_TABLE_NAME = "sys_operational_details_log20190715135252";
    //pro
//    private final String SOURCE_TABLE_NAME = "tm_functionaloperation_log";
//    private final String TARGET_TABLE_NAME = "sys_operational_details_log20190715131637";

    @Override
    public void main(Map<String, String> map) throws Exception {
        Scan scan = new Scan();
        //全表扫描旧表
        JavaPairRDD<ImmutableBytesWritable, Result> oldTableResultRdd = DPHbase.rddRead(SOURCE_TABLE_NAME, scan);

        JavaPairRDD a = oldTableResultRdd.mapToPair(new PairFunction<Tuple2<ImmutableBytesWritable, Result>, ImmutableBytesWritable, Put>() {
            @Override
            public Tuple2<ImmutableBytesWritable, Put> call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                Result result = tuple2._2();
                //整合数据
                byte[] busercode = result.getValue("baseinfo".getBytes(), "usercode".getBytes());
                byte[] bsysname = result.getValue("baseinfo".getBytes(), "sysname".getBytes());
                byte[] btype = result.getValue("baseinfo".getBytes(), "type".getBytes());
                byte[] bdashboardid = result.getValue("baseinfo".getBytes(), "dashboardid".getBytes());
                byte[] bdashboardversion = result.getValue("baseinfo".getBytes(), "dashboardversion".getBytes());
                byte[] bdashboardtype = result.getValue("baseinfo".getBytes(), "dashboardtype".getBytes());
                byte[] bsystimestamp = result.getValue("baseinfo".getBytes(), "systimestamp".getBytes());
                byte[] bsessionid = result.getValue("baseinfo".getBytes(), "sessionid".getBytes());
                byte[] btimestamp = result.getValue("baseinfo".getBytes(), "timestamp".getBytes());
                byte[] bclientip = result.getValue("baseinfo".getBytes(), "clientip".getBytes());
                byte[] blang = result.getValue("baseinfo".getBytes(), "lang".getBytes());
                byte[] bbrowsertype = result.getValue("baseinfo".getBytes(), "browsertype".getBytes());
                byte[] bbrowserversion = result.getValue("baseinfo".getBytes(), "browserversion".getBytes());
                byte[] bterminaltype = result.getValue("baseinfo".getBytes(), "terminaltype".getBytes());
                byte[] bdashboardwh = result.getValue("baseinfo".getBytes(), "dashboardwh".getBytes());
                byte[] bmouseclick = result.getValue("baseinfo".getBytes(), "mouseclick".getBytes());
                byte[] bmousemove = result.getValue("baseinfo".getBytes(), "mousemove".getBytes());
                String usercode = busercode == null ? "" : new String(busercode);
                String sysname = bsysname == null ? "" : new String(bsysname);
                String type = btype == null ? "" : new String(btype);
                String dashboardid = bdashboardid == null ? "" : new String(bdashboardid);
                String dashboardversion = bdashboardversion == null ? "" : new String(bdashboardversion);
                String dashboardtype = bdashboardtype == null ? "" : new String(bdashboardtype);
                String systimestamp = bsystimestamp == null ? "" : new String(bsystimestamp);
                String sessionid = bsessionid == null ? "" : new String(bsessionid);
                String timestamp = btimestamp == null ? "" : new String(btimestamp);
                String clientip = bclientip == null ? "" : new String(bclientip);
                String lang = blang == null ? "" : new String(blang);
                String browsertype = bbrowsertype == null ? "" : new String(bbrowsertype);
                String browserversion = bbrowserversion == null ? "" : new String(bbrowserversion);
                String terminaltype = bterminaltype == null ? "" : new String(bterminaltype);
                String dashboardwh = bdashboardwh == null ? "" : new String(bdashboardwh);
                String mouseclick = bmouseclick == null ? "" : new String(bmouseclick);
                String mousemove = bmousemove == null ? "" : new String(bmousemove);
                String logData = JSONObject.toJSONString(new HashMap<String, Object>() {{
                    put("usercode", usercode);
                    put("sysname", sysname);
                    put("type", type);
                    put("dashboardid", dashboardid);
                    put("dashboardversion", dashboardversion);
                    put("dashboardtype", dashboardtype);
                    put("systimestamp", systimestamp);
                    put("sessionid", sessionid);
                    put("timestamp", timestamp);
                    put("clientip", clientip);
                    put("lang", lang);
                    put("browsertype", browsertype);
                    put("browserversion", browserversion);
                    put("terminaltype", terminaltype);
                    put("dashboardwh", dashboardwh);
                    put("mouseclick", mouseclick);
                    put("mousemove", mousemove);
                }});
                System.out.println(logData);
                String uuid = MD5Builder.getMD516String(UUID.randomUUID().toString().replaceAll("-", ""));
                String logType = mouseclick == null || mouseclick.equals("[]") ? "MOUSEMOVE" : "MOUSECLICK";
                String rowKey = String.format("%02d", RandomUtils.nextInt(1, 11)) + ":" + systimestamp + ":DASHBOARD:" + logType + ":" + uuid;
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("sysname"), Bytes.toBytes(sysname));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("logtype"), Bytes.toBytes(logType));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("eventtime"), Bytes.toBytes(systimestamp));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clientip"), Bytes.toBytes(clientip));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clienttype"), Bytes.toBytes(terminaltype));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clientlang"), Bytes.toBytes(lang));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("loguser"), Bytes.toBytes(usercode));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("logdata"), Bytes.toBytes(logData));
                return new Tuple2<>(new ImmutableBytesWritable(), put);
            }
        });
        DPHbase.rddWrite(TARGET_TABLE_NAME, a);
        DPSparkApp.stop();

    }
}
