package com.tm.dl.javasdk.dpspark;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

@Slf4j
public class DPSparkAppTest implements Serializable {

    public static void main(String[] args) throws Exception {
        DPSparkApp.main(args);
    }
}
