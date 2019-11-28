package com.tm.dl.javasdk.dpspark.common;

import com.tm.dl.javasdk.dpspark.streaming.DPStreaming;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.api.java.JavaInputDStream;

import java.util.Map;
import java.util.function.Consumer;

public abstract class DPSparkBase {
    public abstract void scheduling(Map<String, Object> arrm) throws Exception;
    public abstract void streaming(Map<String, Object> arrm, DPStreaming dpStreaming) throws Exception;
}
