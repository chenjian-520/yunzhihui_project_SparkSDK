package com.tm.dl.javasdk.dpspark.TestDemo;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.streaming.DPStreaming;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/21 12:17
 **/
@Slf4j
public class TestDemo extends DPSparkBase implements Serializable {
    @Override
    public void scheduling(Map<String, Object> arrm) throws Exception {
        DPSparkApp.getContext();

        log.info("============scheduling");
    }

    @Override
    public void streaming(Map<String, Object> arrm, DPStreaming dpStreaming) throws Exception {
        log.info("============streaming");
    }
}
