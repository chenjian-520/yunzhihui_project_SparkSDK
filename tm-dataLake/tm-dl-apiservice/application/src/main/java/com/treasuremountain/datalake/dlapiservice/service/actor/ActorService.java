package com.treasuremountain.datalake.dlapiservice.service.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import com.treasuremountain.datalake.dlapiservice.actor.ActorSystemContext;
import com.treasuremountain.datalake.dlapiservice.actor.KafkaActor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/7.
 * Company: Maxnerva
 * Project: MaxIoT
 */
@Slf4j
@Service
public class ActorService {

    private static final String ACTOR_SYSTEM_NAME = "TMDatalakeAkka";

    @Autowired
    private ActorSystemContext actorContext;

    private ActorSystem system;

    private ActorRef kafkaActor;

    @PostConstruct
    public void init() {
        actorContext.setActorService(this);
        system = ActorSystem.create(ACTOR_SYSTEM_NAME);
        actorContext.setActorSystem(system);

        kafkaActor = system.actorOf(Props.create(new KafkaActor.ActorCreator(actorContext)), "kafkaActor");
        actorContext.setKafkaActor(kafkaActor);

    }

    @PreDestroy
    public void preDestroy(){
        Future<Terminated> status = system.terminate();
        try {
            Terminated terminated = Await.result(status, Duration.Inf());
        } catch (Exception e) {
            log.error("Failed to terminate actor system.", e);
        }
    }
}
