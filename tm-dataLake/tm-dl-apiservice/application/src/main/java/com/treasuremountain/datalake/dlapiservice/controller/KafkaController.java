package com.treasuremountain.datalake.dlapiservice.controller;

import akka.actor.ActorRef;
import com.treasuremountain.datalake.dlapiservice.actor.ActorSystemContext;
import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.common.data.kafka.KafkaMsgDto;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TopicEntity;
import com.treasuremountain.datalake.dlapiservice.common.message.HttpResponseMsg;
import com.treasuremountain.datalake.dlapiservice.config.webconfig.ApiVersion;
import com.treasuremountain.datalake.dlapiservice.service.kafka.producer.KafkaProdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @program: dlapiservice->KafkaController
 * @description: kafka控制器
 * @author: Axin
 * @create: 2019-11-15 23:04
 **/
@RestController
@RequestMapping("/api/dlapiservice/{version}")
public class KafkaController extends BaseController{

    private final static Logger log = LoggerFactory.getLogger(KafkaController.class);

    @Autowired
    private ActorSystemContext actorSystemContext;

    @Autowired
    private KafkaProdService kafkaProdService;

    @RequestMapping(value = "/kafka/async", method = RequestMethod.POST)
    @ApiVersion(1)
    @CrossOrigin
    public void sendKafkaMsgAsync(@RequestBody KafkaMsgDto kafkaMsgDto) throws Exception{
        List<TopicEntity> topicEntityList = new ArrayList<>();
        BusinessCache.topicList.stream().filter(e->e.getTopicName().equals(kafkaMsgDto.getTopic())).forEach(f->{
            topicEntityList.add(f);
        });
        if (!topicEntityList.isEmpty()){
            actorSystemContext.getKafkaActor().tell(kafkaMsgDto, ActorRef.noSender());
        }else {
            log.error("{}","kafka config not exists : topic name is " + kafkaMsgDto.getTopic());
        }
    }


    @RequestMapping(value = "/kafka", method = RequestMethod.POST)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg<Boolean> sendKafkaMsg(@RequestBody KafkaMsgDto kafkaMsgDto) throws Exception{
        List<TopicEntity> topicEntityList = new ArrayList<>();
        BusinessCache.topicList.stream().filter(e->e.getTopicName().equals(kafkaMsgDto.getTopic())).forEach(f->{
            topicEntityList.add(f);
        });
        if (!topicEntityList.isEmpty()){
            HttpResponseMsg<Boolean> httpResponseMsg = new HttpResponseMsg<>();
            Boolean isSend = kafkaProdService.sendMsg(kafkaMsgDto);
            httpResponseMsg.setData(isSend);
            return httpResponseMsg;
        }else {
            log.error("{}","kafka config not exists : topic name is " + kafkaMsgDto.getTopic());
            HttpResponseMsg httpResponseMsg = new HttpResponseMsg();
            httpResponseMsg.setData(false);
            httpResponseMsg.setMsg("kafka config not exists : topic name is " + kafkaMsgDto.getTopic());
            return httpResponseMsg;
        }
    }

}
