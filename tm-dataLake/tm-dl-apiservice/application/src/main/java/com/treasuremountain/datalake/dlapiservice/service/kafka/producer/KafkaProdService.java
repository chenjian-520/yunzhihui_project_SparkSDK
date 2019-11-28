package com.treasuremountain.datalake.dlapiservice.service.kafka.producer;

import com.treasuremountain.datalake.dlapiservice.common.data.kafka.KafkaMsgDto;
import com.treasuremountain.datalake.dlapiservice.service.initialization.InitializationService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @version 1.0
 * @program: dlapiservice->KafkaProdService
 * @description: kafka同步发送消息
 * @author: Axin
 * @create: 2019-11-16 17:09
 **/
@Service
public class KafkaProdService {

    public Boolean sendMsg(KafkaMsgDto msgDto) throws ExecutionException, InterruptedException {
        KafkaProducer<String,String> kafkaProducer = InitializationService.kafkaProducerPool.getObject();
        if (kafkaProducer!=null) {
            ProducerRecord<String, String> record = new ProducerRecord<>(msgDto.getTopic(), msgDto.getMsg());
            KafkaProdRunnable callable = new KafkaProdRunnable(kafkaProducer,record);
            Boolean result = Executors.newFixedThreadPool(InitializationService.sProducerThreadCount).submit(callable).get();
            return result;
        }
        return false;
    }

}
