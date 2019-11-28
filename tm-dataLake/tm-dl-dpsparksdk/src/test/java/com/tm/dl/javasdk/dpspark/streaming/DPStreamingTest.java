package com.tm.dl.javasdk.dpspark.streaming;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.DPSparkAppTest;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.common.ExternalInfoHelper;
import com.tm.dl.javasdk.dpspark.streaming.KafkaSink;
import org.apache.spark.broadcast.Broadcast;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/14 10:30
 **/
public class DPStreamingTest extends DPSparkBase implements Serializable {

    @Override
    public void scheduling(Map<String, Object> arrm) throws Exception {
        //todo  调度执行的spark
        function2();
        function1();
    }

    @Override
    public void streaming(Map<String, Object> arrm, DPStreaming dpStreaming) throws Exception {
        final Broadcast kafkaProducer = DPSparkApp.getContext().broadcast(KafkaSink.apply(new Properties() {{
            putAll(dpStreaming.getKafkaParams());
        }}));
        dpStreaming.startJob(rdd -> {
            rdd.foreachPartition(iterator -> {
                iterator.forEachRemaining(record -> {
                    System.out.println("Receive the kafka data :" + record.value() + "||" + record.toString());
                    //do something and then send by kafkaProducer
                    ((KafkaSink) kafkaProducer.value()).sendAll("targetspark", record.key(), record.value());
                });
            });

            ExternalInfoHelper.externalHbase(null, null, null);
        });
    }

    /**
     * 公共方法1
     */
    private void function1() {

    }

    /**
     * 公共方法2
     */
    private void function2() {
    }
}
