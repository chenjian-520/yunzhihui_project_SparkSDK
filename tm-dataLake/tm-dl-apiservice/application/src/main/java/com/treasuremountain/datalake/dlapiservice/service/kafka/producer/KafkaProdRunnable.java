package com.treasuremountain.datalake.dlapiservice.service.kafka.producer;

import com.treasuremountain.datalake.dlapiservice.service.initialization.InitializationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mortbay.util.ajax.JSON;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @version 1.0
 * @program: dlapiservice->KafkaProdRunnable
 * @description: kafka同步发送消息线程实现类
 * @author: Axin
 * @create: 2019-11-16 15:23
 **/
@Slf4j
public class KafkaProdRunnable implements Callable<Boolean> {

    private KafkaProducer<String, String> producer = null;
    private ProducerRecord<String, String> record = null;

    public KafkaProdRunnable(KafkaProducer<String, String> producer, ProducerRecord<String, String> record) {
        this.producer = producer;
        this.record = record;
    }


    @Override
    public Boolean call() throws Exception {
        Set<Boolean> flagList =new HashSet<>();
        producer.send(record, (metadata, e) -> {
            flagList.add(false);
            if (null != e) {
                log.error("{}"," Kafka Produce send message error " + e);
                log.error("{}"," Kafka Produce send message info: metadata: " + JSON.toString(metadata));
                e.printStackTrace();
            }else {
                if (null != metadata) {
                    flagList.add(true);
                    log.info("{}", "消息发送成功 ：" + String.format("offset: %s, partition:%s, topic:%s  timestamp:%s",
                            metadata.offset(), metadata.partition(), metadata.topic(), metadata.timestamp()));
                }else {
                    flagList.add(false);
                    log.info("{}"," Kafka Produce send message info : metadata is null");
                }
            }
            InitializationService.kafkaProducerPool.returnObject(producer);
        });
        if(flagList.contains(false)) {
            return false;
        }else {
            return true;
        }
    }
}
