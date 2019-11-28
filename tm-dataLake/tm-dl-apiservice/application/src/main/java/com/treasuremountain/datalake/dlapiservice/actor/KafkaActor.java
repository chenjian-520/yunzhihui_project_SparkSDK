package com.treasuremountain.datalake.dlapiservice.actor;

import com.treasuremountain.datalake.dlapiservice.common.data.kafka.KafkaMsgDto;
import com.treasuremountain.datalake.dlapiservice.service.kafka.producer.KafkaProdAsync;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @program: dlapiservice->KafkaActor
 * @description: KafkaActor
 * @author: AxinContextBasedCreator
 * @create: 2019-11-16 09:49
 **/
@Slf4j
public class KafkaActor  extends ContextAwareActor{

    public KafkaActor(ActorSystemContext systemContext) {
        super(systemContext);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(KafkaMsgDto.class, ms -> {
            KafkaProdAsync.sendMsg(ms);
        }).matchAny(t -> {
            log.info("未知的数据类型：" + t);
        }).build();
    }

    public static class ActorCreator extends ContextBasedCreator<KafkaActor> {
        private static final long serialVersionUID = 1L;

        public ActorCreator(ActorSystemContext context) {
            super(context);
        }

        @Override
        public KafkaActor create() throws Exception {
            return new KafkaActor(context);
        }
    }
}
