package com.tm.dl.javasdk.dpspark.hbase;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/14 13:54
 **/
public abstract class DPHbaseExternal {

    /**
     * 读取hbase 扩展表信息,扫描全表
     * 不能被重写
     *
     * @param tableName 主表名
     * @param scan      从表扫描条件
     **/
    public static JavaRDD readExt(String tableName, Scan scan) {
        //todo 提供默认实现

        return null;
    }

    /***
     * 处理主从表数据，并存入hbase扩展表
     * @param mainRdd 主表信息rdd
     * @param externalRdd 从表数据rdd
     * @param tableName 主表名
     * **/
    public abstract JavaRDD external(JavaRDD mainRdd, JavaRDD externalRdd, String tableName);

}
