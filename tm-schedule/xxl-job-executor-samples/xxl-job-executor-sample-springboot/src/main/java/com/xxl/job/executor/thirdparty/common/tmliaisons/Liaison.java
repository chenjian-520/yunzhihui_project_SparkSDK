package com.xxl.job.executor.thirdparty.common.tmliaisons;

import com.google.gson.Gson;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.thirdparty.DPCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class Liaison {

    Gson gson = new Gson();

    public LiaisonResultDto getScheduleInfo(String id) throws Exception {

        String infourl = DPCache.tmmanagementurl + "/api/common/v1/schedule/excuteinfo/" + id;

        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        XxlJobLogger.log(String.format("getScheduleInfo,uri: %s", infourl));

        String rstr = TMHttpClient.doAsyncGet(infourl, header);

        XxlJobLogger.log(String.format("getScheduleInfo,result: %s", rstr));

        return gson.fromJson(rstr, LiaisonResultDto.class);
    }
}
