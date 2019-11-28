package com.xxl.job.executor.system.executor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.HtableIndexEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.IndexlogEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TwoLevelIndexEntity;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.system.initialization.SysInitializationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class SysIndexManagerExec extends BaseSystem {

    private String dlapiUrl = SysInitializationService.dlapiUrl;

    Gson gson = new Gson();

    @Override
    public ReturnT<String> execute(Map<String, String> param, int logId) {

        ReturnT<String> returnT = new ReturnT<>();

        returnT.setCode(200);

        try {
            List<TwoLevelIndexEntity> list = getTwoLevelIndexInfo();

            list.stream().forEach(d -> {

                HtableIndexEntity htableIndexEntity = d.getHtableIndex();

                if (htableIndexEntity.isHbtableIstwoLevelIndex()) {
                    String tableId = d.getHtid();

                    if (isCreateNewIndex(d)) {
                        String createUrl = String.format("%s/api/dlapiservice/v1/datalake/index/%s", dlapiUrl, tableId);
                        try {
                            XxlJobLogger.log(String.format("start create: %s", createUrl));

                            TMHttpClient.doAsyncPut(createUrl, null, "");

                            XxlJobLogger.log("create success");
                        } catch (Exception e) {

                            XxlJobLogger.log(String.format("create error: %s", e.toString()));

                            returnT.setCode(500);
                            returnT.setMsg(e.toString());
                        }
                    }

                    String delIndxName = isDeleteIndex(d);

                    if (StringUtils.isNotBlank(delIndxName)) {
                        String createUrl = String.format("%s/api/dlapiservice/v1/datalake/index/%s/%s", dlapiUrl, tableId, delIndxName);
                        try {
                            XxlJobLogger.log(String.format("start delete: %s", createUrl));

                            TMHttpClient.doAsyncDelete(createUrl, null);

                            XxlJobLogger.log("delete success");
                        } catch (Exception e) {

                            XxlJobLogger.log(String.format("delete error: %s", e.toString()));

                            returnT.setCode(500);
                            returnT.setMsg(e.toString());
                        }
                    }
                }

            });

        } catch (Exception ex) {

            XxlJobLogger.log(String.format("exec error: %s", ex.toString()));

            if (ex instanceof InterruptedException) {
                returnT.setCode(408);
                returnT.setMsg(ex.toString());
            } else {
                returnT.setCode(500);
                returnT.setMsg(ex.toString());
            }
        }

        return returnT;
    }

    @Override
    public ReturnT<String> interrupt(Map<String, String> param, int logId) {
        return null;
    }

    private List<TwoLevelIndexEntity> getTwoLevelIndexInfo() throws Exception {
        String url = String.format("%s/api/dlapiservice/v1/datalake/indexinfo", dlapiUrl);

        String rstr = TMHttpClient.doAsyncGet(url, null);

        List<TwoLevelIndexEntity> rlist = gson.fromJson(rstr, new TypeToken<List<TwoLevelIndexEntity>>() {
        }.getType());

        return rlist;
    }

    private boolean isCreateNewIndex(TwoLevelIndexEntity d) {
        try {
            HtableIndexEntity htableIndexEntity = d.getHtableIndex();
            List<IndexlogEntity> indexlogEntityList = d.getIndexlogList();

            int segmentTime = htableIndexEntity.getHbsegmenttime();
            String currentIndexName = htableIndexEntity.getHbcurrentindexname();

            IndexlogEntity cuttentIndexlog = indexlogEntityList.stream()
                    .filter(e -> e.getIndexlogName().equals(currentIndexName)).findFirst().get();

            Date createTime = new Date(cuttentIndexlog.getIndexlogCreatetime());

            XxlJobLogger.log(String.format("currentIndexName: %s,createTime: %s,segmentTime :%s"
                    , currentIndexName, createTime.toString(), segmentTime));

            int min = getDatePoor(createTime, new Date());

            if (min >= segmentTime) {

                XxlJobLogger.log(String.format("is create,time difference: %s", min));

                return true;
            } else {

                XxlJobLogger.log(String.format("not create,time difference: %s", min));

                return false;
            }
        } catch (Exception ex) {
            XxlJobLogger.log(ex.toString());
            return false;
        }
    }

    private String isDeleteIndex(TwoLevelIndexEntity d) {
        HtableIndexEntity htableIndexEntity = d.getHtableIndex();
        List<IndexlogEntity> indexlogEntityList = d.getIndexlogList();

        int retentiontime = htableIndexEntity.getHbretentiontime();

        //edit ref.tian 201910311416
        int min =0;
        String minIndexName=null;
        if(!indexlogEntityList.isEmpty()){
            IndexlogEntity minIndexlog = indexlogEntityList.stream()
                    .min(((o1, o2) -> new Date(o1.getIndexlogCreatetime()).compareTo(new Date(o2.getIndexlogCreatetime())))).get();
            Date createTime = new Date(minIndexlog.getIndexlogCreatetime());
            minIndexName = minIndexlog.getIndexlogName();

            XxlJobLogger.log(String.format("minIndexName: %s,createTime: %s,retentiontime :%s"
                    , minIndexName, createTime.toString(), retentiontime));

            min = getDatePoor(createTime, new Date());
        }




        if (min >= retentiontime) {

            XxlJobLogger.log(String.format("is delete,time difference: %s", min));

            return minIndexName;
        } else {

            XxlJobLogger.log(String.format("not delete,time difference: %s", min));

            return "";
        }
    }

    private int getDatePoor(Date startDate, Date endDate) {
        long nm = 1000 * 60;

        long diff = endDate.getTime() - startDate.getTime();
        long min = diff / nm;

        return (int) min;
    }
}
