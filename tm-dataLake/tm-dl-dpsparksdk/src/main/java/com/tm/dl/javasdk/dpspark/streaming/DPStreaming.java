package com.tm.dl.javasdk.dpspark.streaming;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.entity.DPKafkaInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;

import java.io.Serializable;
import java.util.*;

/**
 * Description:  streaming流式数据处理基类，主要用于处理流式数据，需实现抽象方法后使用
 * Copyright: © 2019 Maxnerva. All rights reserved.
 * Company: Foxconn
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/8
 */
@SuppressWarnings("Duplicates")
public class DPStreaming implements Serializable {
    private Map kafkaParams;
    private Collection topics;
    static JavaInputDStream<ConsumerRecord<String, Object>> dStream;
    static JavaDStream<ConsumerRecord<String, Object>> windowDStream;

    private static class DPStreamingInstance {
        private static final DPStreaming INSTANCE = new DPStreaming();
    }

    public static final DPStreaming getDPStreaming() {
        return DPStreamingInstance.INSTANCE;
    }

    public DPStreaming() {
    }

    /**
     * 初始化方法，调用时需要执行此方法，
     * 方法会装载对应的kafka配置参数（包含连接地址等信息）和待读取的队列信息
     * 该方法不需要用户实现，但可以重写
     */
    public void init() {
        DPKafkaInfo dpKafkaInfo = DPSparkApp.getDpPermissionManager().initialKafkaSecurityContext();
        kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", dpKafkaInfo.getServerUrl());
        kafkaParams.put("key.deserializer", StringDeserializer.class.getName());
        kafkaParams.put("value.deserializer", StringDeserializer.class.getName());
        kafkaParams.put("key.serializer", StringSerializer.class.getName());
        kafkaParams.put("value.serializer", StringSerializer.class.getName());
        kafkaParams.put("group.id", dpKafkaInfo.getGroupId());
        kafkaParams.put("auto.offset.reset", "earliest");
        kafkaParams.put("enable.auto.commit", "false");
        topics = Arrays.asList(dpKafkaInfo.getTopics().split(","));
        if (dpKafkaInfo.getKafkaConf() != null) {
            kafkaParams.putAll(dpKafkaInfo.getKafkaConf());
        }
    }

    /**
     * 启动流式数据处理任务的主逻辑，
     * 不需要结束任务的逻辑（因为是常驻任务，由spark统一关闭）
     * 该方法不需要用户实现，但是可以重写
     *
     * @throws InterruptedException
     */
    public void startJob(SerializableConsumer<JavaRDD<ConsumerRecord<String, Object>>> streamrddConsumer) throws InterruptedException {
        //Durations.seconds(5) pull data every x s
        DPKafkaInfo kafkaInfo = DPSparkApp.getDPKafkaInfo();
        JavaStreamingContext scc = new JavaStreamingContext(DPSparkApp.getContext(), Durations.seconds(kafkaInfo.getBatchDuration()));
        dStream = KafkaUtils.createDirectStream(scc, LocationStrategies.PreferConsistent(), ConsumerStrategies.Subscribe(topics, kafkaParams));
        if (kafkaInfo.getWindowDurationMultiple() != null) {
            Duration windowDuration = Durations.seconds(kafkaInfo.getWindowDurationMultiple() * kafkaInfo.getBatchDuration());
            Duration sliverDuration = Durations.seconds(kafkaInfo.getSliverDurationMultiple() * kafkaInfo.getBatchDuration());
//            JavaDStream<SerializableRecord> windowDStream = dStream.map(new Function<ConsumerRecord<String, Object>, SerializableRecord>() {
//                @Override
//                public SerializableRecord call(ConsumerRecord<String, Object> consumerRecord) throws Exception {
//                    return new consumerRecord(consumerRecord);
//                }
//            });

            windowDStream = dStream.window(windowDuration, sliverDuration);
            windowDStream.foreachRDD(rdd -> {
                if (!rdd.isEmpty()) {
                    streamrddConsumer.accept(rdd);
                    //提交offset
                    OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
                    ((CanCommitOffsets) dStream.inputDStream()).commitAsync(offsetRanges);
                }
            });
        } else {
            dStream.foreachRDD(rdd -> {
                if (!rdd.isEmpty()) {
                    streamrddConsumer.accept(rdd);
                    //提交offset
                    OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
                    ((CanCommitOffsets) dStream.inputDStream()).commitAsync(offsetRanges);
                }
            });
        }

        scc.start();
        scc.awaitTermination();
    }

    public Map getKafkaParams() {
        return kafkaParams;
    }

    public Collection getTopics() {
        return topics;
    }
}
