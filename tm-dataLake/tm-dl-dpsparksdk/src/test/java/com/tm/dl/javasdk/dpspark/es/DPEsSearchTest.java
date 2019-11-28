package com.tm.dl.javasdk.dpspark.es;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.streaming.DPStreaming;

import java.util.List;
import java.util.Map;

/**
 * Description:  com.tm.dl.javasdk.dpspark.es
 * Copyright: © 2019 Foxconn. All rights reserved.
 * Company: Foxconn
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/21
 */
public class DPEsSearchTest extends DPSparkBase {
    @Override
    public void scheduling(Map<String, Object> arrm) throws Exception {
        DPSparkApp.getDpPermissionManager().initialEsSecurityContext();
        List result = DPEs.queryDsl("sys_spark_index_test20191120142425", "{\"query\":{\"match\":{\"cf1_name\":\"4001\"}}}");
        System.out.println(result.toString());
    }

    @Override
    public void streaming(Map<String, Object> arrm, DPStreaming dpStreaming) throws Exception {

    }
}
