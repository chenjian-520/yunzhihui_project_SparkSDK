package com.treasuremountain.machinecloudreceiver.service.initialization;

import com.treasuremountain.machinecloudreceiver.cache.DatarelationCache;
import com.treasuremountain.machinecloudreceiver.dao.Dao;
import com.treasuremountain.machinecloudreceiver.service.datareceiver.DataReceiverService;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    @Value("${receiver.queryName}")
    private String queryName;

    @Value("${rabbitmq.threadCount}")
    private int threadCount;

    @Value("${dlservice.persistence}")
    private String persistenceUrl;

    @Autowired
    private Dao dao;

    @PostConstruct
    public void init() {
        try {
            dao.init();
            DatarelationCache.loadCache();
            TMHttpClient.init();
            TMRabbitMqOperator.connect(rabbitHost, userName, password, Integer.parseInt(rabbitPort), threadCount);
            TMRabbitMqOperator.queueDeclare(queryName, true, false, false, null);
            DataReceiverService.intReceiver(queryName, persistenceUrl);
            DatarelationCache.autoRefreshCache();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }
}
