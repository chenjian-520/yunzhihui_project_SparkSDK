package com.tm.dl.javasdk.dpspark.mysql;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.spark.api.java.JavaRDD;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/14 14:07
 **/
public abstract class DPMysqlExternal {


    /***
     * 处理主从表数据
     * @param mainRdd 主表信息rdd
     * @param externalRdd 从表数据rdd
     * **/
    public abstract JavaRDD external(JavaRDD mainRdd, JavaRDD externalRdd);

}
