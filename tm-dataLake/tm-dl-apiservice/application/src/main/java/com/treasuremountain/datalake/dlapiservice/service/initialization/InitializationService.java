package com.treasuremountain.datalake.dlapiservice.service.initialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.dao.hbase.HbaseDao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.service.hbase.HbaseDao;
import com.treasuremountain.datalake.dlapiservice.service.kafka.cachepool.KafkaProducerPool;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.hdfs.TMDLHDFSOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gerryzhao on 10/17/2018.
 */
@Service
@EnableScheduling
public class InitializationService {

    private final static Logger log = LoggerFactory.getLogger(InitializationService.class);

    @Value("${hbase.zookeeperQuorum}")
    private String quorum;

    @Value("${hbase.zookeeperClientPort}")
    private String clientPort;

    @Value("${hbasebackup.zookeeperQuorum}")
    private String backupQuorum;

    @Value("${hbasebackup.zookeeperClientPort}")
    private String backupClientPort;

    @Value("${rabbitmq.host}")
    private String rabbitHost;

    @Value("${rabbitmq.port}")
    private String rabbitPort;

    @Value("${rabbitmq.username}")
    private String userName;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${elasticsearch.uri}")
    private String elasticsearchUri;

    @Value("${hdfs.uri}")
    private String hdfsUri;

    @Value("${hdfs.uriback1}")
    private String uriback1;

    @Value("${hdfs.uriback2}")
    private String uriback2;

    @Value("${sysConf.searchThreadCount}")
    private int searchThreadCount;

    @Value("${kafka.prodThreadcount}")
    private int producerThreadCount;

    @Autowired
    private HbaseDao hbaseDao;

    @Autowired
    private Dao dao;

    public static String sBackupQuorum;

    public static String sbackupClientPort;

    public static int sSearchThreadCount;

    public static String sHbaseZookeeperQuorum;

    public static String sHbaseZookeeperClientPort;

    public static HashMap<String,String> hdfsUriMap = new HashMap<>();

    public static MaxHttpClientUtils maxHttpClientUtils = new MaxHttpClientUtils();

    private ExecutorService catchLoad = Executors.newSingleThreadExecutor();

    public static KafkaProducerPool kafkaProducerPool = new KafkaProducerPool();
    public static int sProducerThreadCount;

    @PostConstruct
    public void init() {
        try {

            sBackupQuorum = this.backupQuorum;
            sbackupClientPort = this.backupClientPort;
            sSearchThreadCount = this.searchThreadCount;
            sHbaseZookeeperQuorum = this.quorum;
            sHbaseZookeeperClientPort=this.clientPort;

            sProducerThreadCount = this.producerThreadCount;

            if(maxHttpClientUtils.getHttpclient()==null){
                maxHttpClientUtils.init(60000, 60000, 60000, 2000, 1500);
            }

            if (Dao.sqlSessionFactory == null) {
                dao.init();
            }

            // todo 生产环境需要屏蔽 线程池 ref.tian
//            catchLoad.execute(new Runnable() {
//               @Override
//                public void run() {
//                    try{
                        BusinessCache.loadCache();
                        log.info("loadCache....is ok");
                        BusinessCache.loadHtableInfo();
                        log.info("loadHtableInfo....is ok");
                        BusinessCache.loadTwoLevelIndexConfig();
                        log.info("loadTwoLevelIndexConfig....is ok");
                        BusinessCache.loadTableSegmentConf();
                        log.info("loadTableSegmentConf....is ok");
                        BusinessCache.autoRefreshCache();//启动定时加载逻辑
                        log.info("autoRefreshCache....is ok");

                        kafkaProducerPool.createPool(producerThreadCount);

//                  }catch (Exception e){
//                       log.error(e.toString(), e);
//                   }
//               }
//           });


            hbaseDao.init(quorum, clientPort);
            log.info("hbaseDao....is ok");
            TMRabbitMqOperator.connect(rabbitHost, userName, password, Integer.parseInt(rabbitPort), 9);
            log.info("TMRabbitMqOperator....is ok");
            MTElasticsearchOperator.init(elasticsearchUri);
            log.info("MTElasticsearchOperator....is ok");
            TMDLHDFSOperator.init(hdfsUri,"hadoop");
            hdfsUriMap.put("hdfsUri",hdfsUri);
            log.info("loadHdfs....is ok");
        } catch (Exception ex) {
            log.error("初始化环境及缓存数据异常："+ex.toString(), ex);
        }
    }

    @Scheduled(fixedDelay  = 2000*10)
    public void refreshHdfsUri(){
        try {
            String currentHdfsUri = hdfsUriMap.get("hdfsUri");
            if(currentHdfsUri!=null){
                String regexp = "((hdfs?)?(://))?([^/*:[0-9]]*)(/?.*)";
                String currentNameNode = currentHdfsUri.replaceAll(regexp, "$4");

                String masterNameNode = hdfsUri.replaceAll(regexp,"$4");

                if (currentNameNode.equals(masterNameNode)){
                    refreshHdfsUri(currentNameNode,uriback1,uriback2);
                }else if (currentNameNode.equals(uriback1)){
                    refreshHdfsUri(currentNameNode,masterNameNode,uriback2);
                }else if (currentNameNode.equals(uriback2)){
                    refreshHdfsUri(currentNameNode,uriback1,masterNameNode);
                }
            }


        }catch (Exception ex){
            ex.printStackTrace();
            log.error("",ex);
        }

    }

    public void refreshHdfsUri(String currentNode,String node1,String node2) throws Exception{
        String state = getHdfsServerStatus(currentNode);
        if (!"active".equals(state)){
            String state1 = getHdfsServerStatus(node1);
            if ("active".equals(state1)){
                String back1Uri = "hdfs://" + node1 + ":8020";
                TMDLHDFSOperator.init(back1Uri,"hadoop");
                hdfsUriMap.replace("hdfsUri",back1Uri);
            }else {
                String state2 = getHdfsServerStatus(node2);
                if ("active".equals(state2)){
                    String back2Uri = "hdfs://" + node2 + ":8020";
                    TMDLHDFSOperator.init(back2Uri,"hadoop");
                    hdfsUriMap.replace("hdfsUri",back2Uri);
                }
            }

        }
    }

    public String getHdfsServerStatus(String nameNode){
        String uri = "http://" + nameNode +  ":50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus";
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse hr = null;
        try {
            hr = InitializationService.maxHttpClientUtils.execution(httpGet, null);
            if (hr.getStatusLine().getStatusCode() == 200 || hr.getStatusLine().getStatusCode() == 201){
                String result = EntityUtils.toString(hr.getEntity(), "UTF-8");
                JsonNode jsonNode = DataTools.fromString(result);
                String status = jsonNode.get("beans").get(0).get("State").asText();
                return status;
            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (hr != null) {
                try {
                    hr.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
