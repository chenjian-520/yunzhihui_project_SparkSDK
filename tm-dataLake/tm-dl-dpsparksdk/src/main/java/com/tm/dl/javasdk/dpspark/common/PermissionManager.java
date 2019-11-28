package com.tm.dl.javasdk.dpspark.common;

import com.tm.dl.javasdk.dpspark.common.entity.DBConnectionInfo;
import com.tm.dl.javasdk.dpspark.common.entity.DPKafkaInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.http.nio.reactor.IOReactorException;

import java.io.IOException;

/**
 * Description:  读取一些敏感信息的接口，在每个组件初始化时需要调用此接口的方法，
 * 例如hbase初始化时需要获取zk的连接地址等
 * Copyright: © 2019 Maxnerva. All rights reserved.
 * Company: IISD
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/10/14
 */
public interface PermissionManager {

    /**
     * 根据用户id获取对应的权限id
     *
     * @param dpUserid
     * @return
     */
    String getUserPermission(String dpUserid);

    /**
     * 获取hdfs的根地址
     *
     * @return
     */
    String getRootHdfsUri();

    /**
     * 装载hbase的configuration信息
     *
     * @return
     * @throws IOException
     */
    Configuration initialHbaseSecurityContext() throws IOException;

    /**
     * 装载hdfs的configuration信息
     *
     * @return
     */
    Configuration initialHdfsSecurityContext();

    /**
     * 装载kafka的连接信息
     *
     * @return
     */
    DPKafkaInfo initialKafkaSecurityContext();


    /**
     * 装载es的连接信息
     *
     * @throws IOReactorException
     */
    void initialEsSecurityContext() throws IOReactorException;

    /**
     * 获取mysql的db配置信息
     **/
    DBConnectionInfo getMysqlInfo();

    /***
     * 获取sqlserver的db配置
     * **/
    DBConnectionInfo getSqlserverInfo();

    /**
     * 根据hbase表名获取对应的index名
     */
    String getCurrentIndexName(String hbaseTable);
}
