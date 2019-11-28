package com.tm.dl.javasdk.dpspark.streaming;

import com.tm.dl.javasdk.dpspark.hbase.DPHbaseExternal;
import org.apache.spark.api.java.JavaRDD;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/14 16:02
 **/
public class DTVIDPHbaseExternal extends DPHbaseExternal {


    @Override
    public JavaRDD external(JavaRDD mainRdd, JavaRDD externalRdd, String tableName) {
        return null;
    }

}
