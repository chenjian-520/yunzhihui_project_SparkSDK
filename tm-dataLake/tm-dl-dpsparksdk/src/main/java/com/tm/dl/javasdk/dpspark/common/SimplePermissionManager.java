package com.tm.dl.javasdk.dpspark.common;

import com.tm.dl.javasdk.dpspark.common.entity.DBConnectionInfo;
import com.tm.dl.javasdk.dpspark.common.entity.DPKafkaInfo;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.http.nio.reactor.IOReactorException;

import java.io.Serializable;

/**
 * Description:  com.tm.dl.javasdk.dpspark.common
 * Copyright: © 2019 Maxnerva. All rights reserved.
 * Company: IISD
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/10/14
 */
public class SimplePermissionManager implements PermissionManager,Serializable {
    @Override
    public String getUserPermission(String dpUserid) {
//        return dpUserid;
        return "hadoop";
    }

    @Override
    public String getRootHdfsUri() {
        return "hdfs://tmmanagement:8020";
    }

    @Override
    public Configuration initialHbaseSecurityContext() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "localhost");
        configuration.set("hbase.zookeeper.clientPort", "2181");
        return configuration;
    }

    @Override
    public Configuration initialHdfsSecurityContext() {
        Configuration conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.setBoolean("dfs.support.append", true);
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
        return conf;
    }

    @Override
    public DPKafkaInfo initialKafkaSecurityContext() {
        DPKafkaInfo dpKafkaInfo = new DPKafkaInfo();
        dpKafkaInfo.setServerUrl("localhost:8080");
        dpKafkaInfo.setGroupId("user1");
        dpKafkaInfo.setTopics("sparktest");
        return dpKafkaInfo;
    }

    @Override
    public void initialEsSecurityContext() throws IOReactorException {
        MTElasticsearchOperator.init("http://localhost:9200");
    }

    @Override
    public DBConnectionInfo getMysqlInfo() {
        DBConnectionInfo dbConnectionInfo=new DBConnectionInfo();
        dbConnectionInfo.setPassword("Foxconn!@3");
        dbConnectionInfo.setUrl("jdbc:mysql://10.60.136.145:3306/test?useSSL=false");//开发环境
        dbConnectionInfo.setUsername("maxadmin");
        return dbConnectionInfo;
    }

    @Override
    public DBConnectionInfo getSqlserverInfo() {
        return null;
    }

    @Override
    public String getCurrentIndexName(String hbaseTable) {
        return null;
    }
}
