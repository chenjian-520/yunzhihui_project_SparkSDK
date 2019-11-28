package com.treasuremountain.datalake.dlapiservice.service.kafka.cachepool;

import com.treasuremountain.datalake.dlapiservice.config.kafkaconfig.ReadKafkaPropertiesUtil;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

/**
 * @version 1.0
 * @program: dlapiservice->KafkaProducerPool
 * @description: KafkaProducerPool
 * @author: Axin
 * @create: 2019-11-16 16:44
 **/
public class KafkaProducerPool extends ObjectPool<KafkaProducer<String,String>> {

    @Override
    public PooledObject<KafkaProducer<String,String>> create(){
        Properties properties = ReadKafkaPropertiesUtil.getProperties();
        // 实例化produce
        KafkaProducer<String, String> kp = new KafkaProducer<String, String>(properties);
        return new PooledObject<KafkaProducer<String,String>>(kp);
    }

}
