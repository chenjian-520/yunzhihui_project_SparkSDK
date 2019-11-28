package com.tm.dl.javasdk.dpspark.mysql;

import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.streaming.DPStreaming;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/15 17:14
 **/
public class MysqlTest extends DPSparkBase implements Serializable {

    @Override
    public void scheduling(Map<String, Object> arrm) throws Exception {
        DPMysql.rddRead("select 1");
    }

    @Override
    public void streaming(Map<String, Object> arrm, DPStreaming dpStreaming) throws Exception {

    }
}
