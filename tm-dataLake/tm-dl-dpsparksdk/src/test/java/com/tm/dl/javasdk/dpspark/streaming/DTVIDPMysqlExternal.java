package com.tm.dl.javasdk.dpspark.streaming;

import com.tm.dl.javasdk.dpspark.mysql.DPMysqlExternal;
import org.apache.spark.api.java.JavaRDD;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/14 16:00
 **/
public class DTVIDPMysqlExternal extends DPMysqlExternal {

    @Override
    public JavaRDD external(JavaRDD mainRdd, JavaRDD externalRdd) {
        return null;
    }
}
