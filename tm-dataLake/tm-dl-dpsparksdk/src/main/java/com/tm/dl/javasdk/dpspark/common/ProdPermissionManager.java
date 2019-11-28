package com.tm.dl.javasdk.dpspark.common;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.entity.DBConnectionInfo;
import com.tm.dl.javasdk.dpspark.common.entity.DPKafkaInfo;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.message.MTHttpCallbackParms;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.http.nio.reactor.IOReactorException;

import javax.security.auth.Subject;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Description:  com.tm.dl.javasdk.dpspark.common
 * Copyright: © 2019 Maxnerva. All rights reserved.
 * Company: IISD
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/10/14
 */
public class ProdPermissionManager implements PermissionManager, Serializable {
    //tmmanagement  dlap1
    private static String managementpermissionurl = "http://localhost:9102/api/common/v1/permission/user/";
    private static String dlapiserviceurl = "http://localhost:8089/api/common/v1/hbaseconfig/realtable/";
    private ObjectMapper om = new ObjectMapper();

    @Override
    public String getUserPermission(String dpUserid) {
        /*CountDownLatch countDownLatch = new CountDownLatch(1);
        final String[] restulstr = new String[1];
        try {
            getUserPermission(dpUserid, countDownLatch, new Consumer<String>() {
                @Override
                public void accept(String s) {

                    restulstr[0] = s;
                    countDownLatch.countDown();
                    return;
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            countDownLatch.countDown();
            return "";
        }
        if (restulstr[0] == null || restulstr[0].isEmpty()) {
            throw new SecurityException("Access decline.User permission not found.");
        }
        return JSON.parseObject(restulstr[0]).getString("permissionUsername");*/

        return "hadoop";
    }

    @Override
    public String getCurrentIndexName(String hbaseTable) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final String[] restulstr = new String[1];
        try {
            getIndexName(hbaseTable, countDownLatch, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    restulstr[0] = s;
                    countDownLatch.countDown();
                    return;
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            countDownLatch.countDown();
            return "";
        }
        if (restulstr[0] == null || restulstr[0].isEmpty()) {
            throw new SecurityException("hbase table index not found.");
        }
        return JSON.parseObject(restulstr[0]).getString("hbcurrentindexname");
    }

    @Override
    public String getRootHdfsUri() {
        return "hdfs://sdkhdfs:8020";
    }

    @Override
    public Configuration initialHbaseSecurityContext() throws IOException {
        initialEsSecurityContext();
        String user = DPSparkApp.getDpUserPermissionStr();
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "zkQuorum1,zkQuorum2,zkQuorum3");
        configuration.set("hbase.zookeeper.clientPort", "2181");
        System.getProperties().setProperty("HADOOP_USER_NAME", user);
        UserGroupInformation.loginUserFromSubject((Subject) null);
        return configuration;
    }

    @Override
    public Configuration initialHdfsSecurityContext() {
        return null;
    }

    @Override
    public DPKafkaInfo initialKafkaSecurityContext() {
        DPKafkaInfo dpKafkaInfo = DPSparkApp.getDPKafkaInfo();
        dpKafkaInfo.setServerUrl("10.60.136.154:9092");
        return dpKafkaInfo;
    }

    @Override
    public void initialEsSecurityContext() throws IOReactorException {
        MTElasticsearchOperator.init("http://es1:9200,http://es2:9200,http://es3:9200");
    }

    @Override
    public DBConnectionInfo getMysqlInfo() {
        DBConnectionInfo dbConnectionInfo = new DBConnectionInfo();
        dbConnectionInfo.setPassword("Foxconn!@3");
        dbConnectionInfo.setUrl("jdbc:mysql://10.60.136.145:3306/test?useSSL=false");
        dbConnectionInfo.setUsername("maxadmin");
        return dbConnectionInfo;
    }

    @Override
    public DBConnectionInfo getSqlserverInfo() {
        return null;
    }


    private void getIndexName(String tableName, CountDownLatch countDownLatch, Consumer<String> consumer) throws IOReactorException {
        try {
            TMHttpClient.init();
            HashMap<String, String> header = new HashMap<>();
            header.putIfAbsent("Content-Type", "application/json");
            TMHttpClient.doGet(dlapiserviceurl + tableName, header, new Consumer<MTHttpCallbackParms>() {
                @Override
                public void accept(MTHttpCallbackParms o) {
                    if (o.getStatusCode() == 200) {
                        try {
                            JsonNode jsonNode = om.readTree(o.getResponseBody());
                            JsonNode datanode = jsonNode.get("data");
                            if (datanode == null || datanode.size() == 0) {
                                consumer.accept("");
                            } else {
                                consumer.accept(datanode.toString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            consumer.accept("");
                        }
                    } else {
                        consumer.accept("");
                    }
                }
            });
        } catch (IOReactorException e) {
            System.out.println("Httpclient 初始化异常");
            consumer.accept("");
        }
    }


    private void getUserPermission(String dpuserid, CountDownLatch countDownLatch, Consumer<String> consumer) throws IOReactorException {
        try {
            TMHttpClient.init();
            HashMap<String, String> header = new HashMap<>();
            header.putIfAbsent("Content-Type", "application/json");
            TMHttpClient.doGet(managementpermissionurl + dpuserid, header, new Consumer<MTHttpCallbackParms>() {
                @Override
                public void accept(MTHttpCallbackParms o) {
                    if (o.getStatusCode() == 200) {
//                        System.out.print(o.getResponseBody());
                        try {
                            JsonNode jsonNode = om.readTree(o.getResponseBody());
                            JsonNode datanode = jsonNode.get("data");
                            if (datanode == null || datanode.size() == 0) {
                                consumer.accept("");
                            } else {
                                consumer.accept(datanode.toString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            consumer.accept("");
                        }
                    } else {
                        consumer.accept("");
                    }
                }
            });
        } catch (IOReactorException e) {
            System.out.println("Httpclient 初始化异常");
            consumer.accept("");
        }
    }
}
