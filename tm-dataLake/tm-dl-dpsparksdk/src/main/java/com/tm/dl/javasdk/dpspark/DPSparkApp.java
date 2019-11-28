package com.tm.dl.javasdk.dpspark;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.google.gson.Gson;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.common.PermissionManager;
import com.tm.dl.javasdk.dpspark.common.entity.DPKafkaInfo;
import com.tm.dl.javasdk.dpspark.common.entity.ExternalInfoDto;
import com.tm.dl.javasdk.dpspark.hbase.DPHbaseExternal;
import com.tm.dl.javasdk.dpspark.mysql.DPMysqlExternal;
import com.tm.dl.javasdk.dpspark.streaming.DPStreaming;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.util.CollectionAccumulator;
import org.slf4j.LoggerFactory;
import scala.Serializable;

import java.util.Map;

@Slf4j
public class DPSparkApp implements Serializable {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DPSparkApp.class);
    private static Gson gson = new Gson();
    public static PermissionManager permissionManager;
    //    public static CollectionAccumulator<SparkSession> sessionBroadcast;
    public static CollectionAccumulator<SparkSession> sessionBroadcast;
    public static CollectionAccumulator<JavaSparkContext> contextBroadcast;
    public static CollectionAccumulator<DPMysqlExternal> dpMysqlExternalCollectionAccumulator;//mysql 扩展信息操作实现逻辑
    public static CollectionAccumulator<DPHbaseExternal> dpHbaseExternalCollectionAccumulator;//hbase 扩展信息操作实现类

    public static Broadcast<String> dpuseridBroadcast;
    public static Broadcast<PermissionManager> permissionBroadcast;// 权限管理器
    public static Broadcast<String> permissionStrBroadcast;// 用户权限数据
    public static Broadcast<DPKafkaInfo> kafkaInfoBroadcast;// 用户权限数据

    public static void main(String[] args) throws Exception {
        //===================获取args参数===================
        String arg = args[0];
        System.out.println("parm:" + arg);
        JSONObject argmap = JSON.parseObject(arg);
        String runClass = argmap.getString("dprunclass");
        String dpuserid = argmap.getString("dpuserid");
        String dpappName = argmap.getString("dpappName");
        String dpmaster = argmap.getString("dpmaster");
        String dpType = argmap.getString("dpType");// spark程序类型，streaming/schedule
        String pmClass = argmap.getString("pmClass"); //pm接口的实现类的全路径名
        String callSource = argmap.getString("callSource");
        String streamingParams = argmap.getString("dpStreaming");
        //格式{topics:xx,batchDuration:xx,windowDuration:xx,sliverDuration:xx,kafkaConf:{xx:xx}}
        //解析streaming参数
        DPKafkaInfo dpKafkaInfo = JSON.parseObject(streamingParams, DPKafkaInfo.class);
        //externalinfoclass:扩展信息实现类，json格式：{\"hbaseExtClass\":[\"com.tm.dl.javasdk.dpspark.sqlserver.DTVIDPHbaseExternal\",\"com.tm.dl.javasdk.dpspark.sqlserver.DTVIDPHbaseExternal\"],\"mysqlExtClass\":[\"com.tm.dl.javasdk.dpspark.sqlserver.DTVIDPMysqlExternal\"]}
        String externalinfoclass = argmap.getString("externalinfoclass");

        //=================获取spark context================
        SparkSession spark = SparkSession.builder()
                .appName(dpappName)
                .master(dpmaster)
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .getOrCreate();

        SparkContext context = spark.sparkContext();
        JavaSparkContext jcontext = JavaSparkContext.fromSparkContext(context);

        //================将必要变量广播出去===================
        contextBroadcast = context.collectionAccumulator("jcontext");
        contextBroadcast.add(jcontext);
        sessionBroadcast = context.collectionAccumulator("spark");
        sessionBroadcast.add(spark);
        dpuseridBroadcast = jcontext.broadcast(dpuserid);
        kafkaInfoBroadcast = jcontext.broadcast(dpKafkaInfo);
        //=========根据args参数获取对应的pm接口的实现类===========
        permissionManager = (PermissionManager) Class.forName(pmClass).newInstance();
        permissionBroadcast = jcontext.broadcast(permissionManager);

        //===============获取用户权限信息，并广播================
        String dppermissionStr = permissionManager.getUserPermission(getDpuserid());
        permissionStrBroadcast = jcontext.broadcast(dppermissionStr);
        //==============获取扩展类具体实现并广播ref.tian===========//
        if (externalinfoclass != null && !externalinfoclass.isEmpty()) {
            ExternalInfoDto externalInfoDto = gson.fromJson(externalinfoclass, ExternalInfoDto.class);
            if (externalInfoDto.getMysqlExt() != null && !externalInfoDto.getMysqlExt().isEmpty()) {
                dpMysqlExternalCollectionAccumulator = context.collectionAccumulator("mysqlexternalinfo");
                for (String mysqlextclass : externalInfoDto.getMysqlExt()) {
                    if (mysqlextclass != null && !mysqlextclass.isEmpty()) {
                        DPMysqlExternal dpMysqlExternal = (DPMysqlExternal) Class.forName(mysqlextclass).newInstance();
                        dpMysqlExternalCollectionAccumulator.add(dpMysqlExternal);
                    }
                }
            }
            if (externalInfoDto.getHbaseExt() != null && !externalInfoDto.getHbaseExt().isEmpty()) {
                dpHbaseExternalCollectionAccumulator = context.collectionAccumulator("hbaseexternalinfo");
                for (String hbaseextclass : externalInfoDto.getHbaseExt()) {
                    if (hbaseextclass != null && !hbaseextclass.isEmpty()) {
                        DPHbaseExternal dpHbaseExternal = (DPHbaseExternal) Class.forName(hbaseextclass).newInstance();
                        dpHbaseExternalCollectionAccumulator.add(dpHbaseExternal);
                    }
                }
            }
        }
        //==========区分streaming运行方式与普通运行方式===========
        if ("streaming".equals(dpType)) {
            DPStreaming dpStreaming = new DPStreaming();
            dpStreaming.init();
            Class<?> aClass = Class.forName(runClass);
            DPSparkBase dpSparkBase = (DPSparkBase) aClass.newInstance();
            dpSparkBase.streaming(argmap, dpStreaming);
        } else {
            //todo  默认都是 schedule 启动的spark
            if (callSource == null) {
                Class<?> aClass = Class.forName(runClass);
                DPSparkBase dpSparkBase = (DPSparkBase) aClass.newInstance();
                dpSparkBase.scheduling(argmap);
            } else {
                LOG.info("the " + callSource + " sdk is called ");
            }
        }
    }

    public static JavaSparkContext getContext() {
        return contextBroadcast.value().get(0);
    }

    public static SparkSession getSession() {
        return sessionBroadcast.value().get(0);
    }

    public static DPKafkaInfo getDPKafkaInfo() {
        return kafkaInfoBroadcast.value();
    }

    public static void stop() {
        getContext().stop();
    }

    public static String getDpuserid() {
        return dpuseridBroadcast.value();
    }

    public static PermissionManager getDpPermissionManager() {
        return permissionBroadcast.value();
    }

    public static String getDpUserPermissionStr() {
        return permissionStrBroadcast.value();
    }

//    public static boolean haveHbaseTablePermission(String tableName, String permission) {
//        try {
//            //edit by ref.tian 201904181105 传递参数调整
//            log.info("=======" + getDpPermission() + "=========" + tableName + "=========" + permission);
//            return BigdataPermission.getInstance().haveHbaseTablePermission(getDpPermission(), tableName, permission);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public static boolean haveHdfsPathPermission(String path, String permission) {
//        try {
//            //edit by ref.tian 201904181105 传递参数调整
//            return BigdataPermission.getInstance().haveHdfsPathPermission(getDpPermission(), path, permission);
//        } catch (Exception e) {
//            return false;
//        }
//    }
}
