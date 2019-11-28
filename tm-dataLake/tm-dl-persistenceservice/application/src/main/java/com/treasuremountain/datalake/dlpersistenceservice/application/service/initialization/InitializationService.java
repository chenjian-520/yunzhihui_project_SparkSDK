package com.treasuremountain.datalake.dlpersistenceservice.application.service.initialization;

import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.dao.hbase.HbaseDao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlpersistenceservice.application.service.datareceiver.DataReceiverService;
import com.treasuremountain.datalake.dlpersistenceservice.application.service.datareceiver.IndexReceiverService;
import com.treasuremountain.datalake.dlpersistenceservice.application.service.datareceiver.MergeReceiverService;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by gerryzhao on 10/21/2018.
 */
@Service
public class InitializationService {
    private final static Logger log = LoggerFactory.getLogger(InitializationService.class);

    @Value("${hbase.zookeeperQuorum}")
    private String quorum;

    @Value("${hbase.zookeeperClientPort}")
    private String clientPort;

    @Value("${rabbitmq.host}")
    private String rabbitHost;

    @Value("${rabbitmq.port}")
    private String rabbitPort;

    @Value("${rabbitmq.username}")
    private String userName;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${receiver.dataQuery}")
    private String dataQuery;

    @Value("${receiver.mergeQuery}")
    private String mergeQuery;

    @Value("${receiver.indexQuery}")
    private String indexQuery;


    @Value("${receiver.packageCount}")
    private int packageCount;

    @Value("${receiver.sendRate}")
    private long sendRate;

    @Value("${elasticsearch.uri}")
    private String elasticsearchUri;

    @Value("${rabbitmq.threadCount}")
    private int threadCount;

    @PostConstruct
    public void init() {
        try {
            Dao dao = new Dao();
            dao.init();
            HbaseDao hbaseDao = new HbaseDao();
            hbaseDao.init(quorum, clientPort);
            BusinessCache.loadCache();
            BusinessCache.loadTableSegmentConf();
            BusinessCache.loadTwoLevelIndexConfig();
            MTElasticsearchOperator.init(elasticsearchUri);
            TMRabbitMqOperator.connect(rabbitHost, userName, password, Integer.parseInt(rabbitPort), threadCount);

            TMRabbitMqOperator.queueDeclare(dataQuery, true, false, false, null);
            MergeReceiverService.intReceiver(dataQuery);

            TMRabbitMqOperator.queueDeclare(mergeQuery, true, false, false, null);
            DataReceiverService.intReceiver(mergeQuery, indexQuery);

            TMRabbitMqOperator.queueDeclare(indexQuery, true, false, false, null);
            IndexReceiverService.intReceiver(indexQuery);

            MergeReceiverService.autoSentMsg(packageCount, mergeQuery, sendRate);

            BusinessCache.autoRefreshCache();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }
}
