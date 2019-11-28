package com.tm.dl.javasdk.dpspark.common;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.hbase.DPHbaseExternal;
import com.tm.dl.javasdk.dpspark.mysql.DPMysqlExternal;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.catalyst.optimizer.LikeSimplification;
import org.codehaus.janino.Java;

import java.util.List;
import java.util.ListResourceBundle;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/14 16:40
 **/
@Slf4j
public class ExternalInfoHelper {

    /**
     * 執行hbase扩展信息读取逻辑
     *
     * @param tableName hbase表操作的主表的名字，扩展表名为后面加_ext
     */
    public static JavaRDD readHbaseExt(String tableName, Scan scan) {
        return DPHbaseExternal.readExt(tableName + "_ext", scan);
    }

    /**
     * @param mainRdd     主表信息rdd
     * @param externalRdd 扩展信息rdd
     * @param tableName   主表名
     * @remark 只能有一个实现被执行并返回结果，其他实现需要返回null
     ***/
    public static JavaRDD externalHbase(JavaRDD mainRdd, JavaRDD externalRdd, String tableName) {
        List<DPHbaseExternal> dpHbaseExternals = DPSparkApp.dpHbaseExternalCollectionAccumulator.value();
        for (DPHbaseExternal dphbaseext : dpHbaseExternals) {
            JavaRDD resultRdd = dphbaseext.external(mainRdd, externalRdd, tableName);
            if (resultRdd != null) {
                return resultRdd;
            }
        }
        return null;
    }
    /**
     * @param mainRdd 主表rdd
     * @param externalRdd 扩展表rdd
     * @remark 只能有一个实现被执行并返回结果，其他实现需要返回null
     * **/
    public static JavaRDD externalMysql(JavaRDD mainRdd, JavaRDD externalRdd) {
        List<DPMysqlExternal> dpMysqlExternals = DPSparkApp.dpMysqlExternalCollectionAccumulator.value();
        for (DPMysqlExternal dpmysqlext : dpMysqlExternals) {
            JavaRDD resultRdd = dpmysqlext.external(mainRdd, externalRdd);
            if (resultRdd != null) {
                return resultRdd;
            }
        }
        return null;
    }
}
