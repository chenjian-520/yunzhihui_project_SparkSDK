package com.treasuremountain.machinecollector.service.initialization;

import com.treasuremountain.machinecollector.cache.IoTDataCache;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class InitializationService {

    private final static Logger log = LoggerFactory.getLogger(InitializationService.class);

    @Value("${rabbitmq.host}")
    private String rabbitHost;

    @Value("${rabbitmq.port}")
    private String rabbitPort;

    @Value("${rabbitmq.username}")
    private String userName;

    @Value("${rabbitmq.password}")
    private String password;

    @PostConstruct
    public void init() {
        try {
            TMRabbitMqOperator.connect(rabbitHost, userName, password, Integer.parseInt(rabbitPort), 9);
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }
}
