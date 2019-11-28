package com.xxl.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@JobHandler(value="PinkEyeJobHandler")
@Component
public class PinkEyeJobHandler extends IJobHandler {
    @Override
    public ReturnT<String> execute(String param, int logId) throws Exception {
        try {
            XxlJobLogger.log(String.format("param:%s logid:%s", param, logId));
            JSONObject parasObj = JSON.parseObject(param);

            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

            String result = TMHttpClient.doAsyncGet(String.valueOf(parasObj.get("url")), header);

            XxlJobLogger.log(result);
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log(e);
            return FAIL;
        }
    }
}

