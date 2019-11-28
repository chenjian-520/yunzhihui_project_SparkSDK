package com.treasuremountain.datalake.dlapiservice.actor;

//import akka.actor.ActorRef;
//import akka.actor.ActorSystem;
//import com.maxiot.websocket.extensions.mqtt.MessageConsumer;
//import com.maxiot.websocket.server.maxiotcatch.RestApiCatch;
//import com.maxiot.websocket.server.service.ActorService;
//import com.maxiot.websocket.server.service.ChannelHttpClient;
//import com.maxiot.websocket.server.service.GsonCommon;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.treasuremountain.datalake.dlapiservice.service.actor.ActorService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/7.
 * Company: Maxnerva
 * Project: MaxIoT
 */
@Component
@Order(3)
public class ActorSystemContext {

    private static final String AKKA_CONF_FILE_NAME = "actor-system.conf";

    @Getter
    @Setter
    private ActorService actorService;

    @Getter
    @Setter
    private ActorSystem actorSystem;

    @Getter
    @Setter
    private ActorRef kafkaActor;

    @Getter
    private final Config config;

    public ActorSystemContext() {
        config = ConfigFactory.parseResources(AKKA_CONF_FILE_NAME).withFallback(ConfigFactory.load());
    }
}
