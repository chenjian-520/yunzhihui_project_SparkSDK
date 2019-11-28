package com.xxl.job.executor.system.executor;

import com.google.gson.Gson;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.system.entity.CarteTaskCodeDto;
import com.xxl.job.executor.system.entity.CarteTaskDetailInfo;
import com.xxl.job.executor.system.entity.CarteTaskInfo;
import com.xxl.job.executor.thirdparty.DPCache;
import com.xxl.job.executor.thirdparty.common.tmliaisons.LiaisonResultDto;

import java.util.HashMap;
import java.util.Map;

public class SysCleanCarteCache extends BaseSystem {

    private String managementUrl = DPCache.tmmanagementurl;
    private String computationUrl = DPCache.computationUrl;

    Gson gson = new Gson();

    @Override
    public ReturnT<String> execute(Map<String, String> param, int logId) {

        ReturnT<String> returnT = new ReturnT<>();
        returnT.setCode(200);

        CarteTaskCodeDto codeDto = getCarteTaskCode(managementUrl);

        XxlJobLogger.log(String.format("getCarteTaskCode %s %s", codeDto.isResult(), codeDto.getMsg()));

        codeDto.getData().forEach(d -> {
            try {
                CarteTaskInfo info = getTaskInfo(managementUrl, d);

                boolean isResult = info.isResult();

                XxlJobLogger.log(String.format("getTaskInfo %s %s", isResult, info.getMsg()));

                if (isResult) {
                    LiaisonResultDto resultDto = cleanCarteCache(computationUrl, info.getData());

                    if (resultDto.isResult()) {
                        XxlJobLogger.log(String.format("%s cleaned", d));
                    } else {
                        XxlJobLogger.log(String.format("clean error,code:%s,msg:%s", d, resultDto.getMsg()));
                        returnT.setCode(500);
                    }
                }
            } catch (Exception ex) {
                XxlJobLogger.log(String.format("clean error,code:%s,msg:%s", d, ex));
                returnT.setCode(500);
            }
        });

        return returnT;
    }

    @Override
    public ReturnT<String> interrupt(Map<String, String> param, int logId) {
        return null;
    }

    private CarteTaskCodeDto getCarteTaskCode(String url) {
        try {
            String cpurl = String.format("%s/api/common/v1/kettle/schedules", url);

            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

            String result = TMHttpClient.doAsyncGet(cpurl, header);

            return gson.fromJson(result, CarteTaskCodeDto.class);
        } catch (Exception ex) {
            XxlJobLogger.log("getCarteTaskCode error,msg:%s", ex);
            return null;
        }
    }

    private CarteTaskInfo getTaskInfo(String url, String code) {
        try {
            String cpurl = String.format("%s/api/common/v1/kettle/cleanupTrans/%s", url, code);

            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

            String result = TMHttpClient.doAsyncGet(cpurl, header);

            return gson.fromJson(result, CarteTaskInfo.class);
        } catch (Exception ex) {
            XxlJobLogger.log("getTaskInfo error,msg:%s", ex);
            return null;
        }
    }

    private LiaisonResultDto cleanCarteCache(String url, CarteTaskDetailInfo data) throws Exception {
        String cpurl = String.format("%s/api/common/v1/kettle/cleanupTrans", url);

        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        String result = TMHttpClient.doAsyncPost(cpurl, header, gson.toJson(data));

        return gson.fromJson(result, LiaisonResultDto.class);
    }
}
