package com.treasuremountain.datalake.dlapiservice.service.kafka.producer;

import com.treasuremountain.datalake.dlapiservice.common.data.kafka.KafkaMsgDto;
import com.treasuremountain.datalake.dlapiservice.config.kafkaconfig.ReadKafkaPropertiesUtil;
import com.treasuremountain.datalake.dlapiservice.service.initialization.InitializationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.mortbay.util.ajax.JSON;

import java.util.Properties;

/**
 * @version 1.0
 * @program: dlapiservice->KafkaProdAsync
 * @description: 异步producer
 * @author: Axin
 * @create: 2019-11-16 13:22
 **/
@Slf4j
public class KafkaProdAsync {

    /**
     * 发送消息
     * @param msgDto
     */
    public static void sendMsg(KafkaMsgDto msgDto){
        Properties properties = ReadKafkaPropertiesUtil.getProperties();
        // 实例化produce
        KafkaProducer<String, String> kp = InitializationService.kafkaProducerPool.getObject();

        if (kp != null) {
            // 消息封装
            ProducerRecord<String, String> pr = new ProducerRecord<>(msgDto.getTopic(), msgDto.getMsg());

            kp.send(pr, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    if (null != e) {
                        log.error("{}", " Kafka Produce send message error " + e);
                        log.error("{}", " Kafka Produce send message info: metadata: " + JSON.toString(metadata));
                        e.printStackTrace();
                    }
                }
            });

            InitializationService.kafkaProducerPool.returnObject(kp);
        }

//        //关闭producer
//        kp.close();

    }
}
