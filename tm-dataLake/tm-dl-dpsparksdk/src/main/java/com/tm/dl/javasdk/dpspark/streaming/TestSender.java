package com.tm.dl.javasdk.dpspark.streaming;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;

/**
 * Description:  com.tm.dl.javasdk.dpspark.streaming
 * Copyright: © 2019 Maxnerva. All rights reserved.
 * Company: IISD
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/11
 */
public class TestSender extends Thread {
    //    private KafkaSink kafkaSink;
    private KafkaProducer<String, String> producer;

    public TestSender() {
        Properties config = new Properties();
        config.put("bootstrap.servers", "10.60.136.154:9092");
        config.put("key.serializer", StringSerializer.class.getName());
        config.put("value.serializer", StringSerializer.class.getName());
        config.put("acks", "all");
        config.put("retries", 0);
        config.put("linger.ms", 1);
//        kafkaSink = KafkaSink.apply(config);
        producer = new KafkaProducer<String, String>(config);

    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            producer.send(new ProducerRecord<String, String>("sparktest1", "val" + i));
            System.out.println(" send messages :" + i);
//            kafkaSink.send("sparktest1", "key" + i, "value" + i);
            i++;
        }
    }
}
