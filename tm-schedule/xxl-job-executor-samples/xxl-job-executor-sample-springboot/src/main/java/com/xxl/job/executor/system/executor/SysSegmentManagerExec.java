package com.xxl.job.executor.system.executor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.HtableSegmentEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TableLogEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TableSegmentEntity;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.system.initialization.SysInitializationService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SysSegmentManagerExec extends BaseSystem {

    private String dlapiUrl = SysInitializationService.dlapiUrl;

    Gson gson = new Gson();


    @Override
    public ReturnT<String> execute(Map<String, String> param, int logId) {

        ReturnT<String> returnT = new ReturnT<>();

        returnT.setCode(200);

        try {
            List<TableSegmentEntity> list = getTableSegmentInfo();

            list.stream().forEach(d -> {
                HtableSegmentEntity htableSegmentEntity = d.getHtableSegment();

                if (htableSegmentEntity.isHbtableIstablesegment()) {
                    String tableId = d.getHtid();
                    if (isCreateNewTable(d)) {
                        String createUrl = String.format("%s/api/dlapiservice/v1/datalake/segment/%s", dlapiUrl, tableId);

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

    private boolean isCreateNewTable(TableSegmentEntity d) {
        HtableSegmentEntity htableSegmentEntity = d.getHtableSegment();
        List<TableLogEntity> tableLogEntityList = d.getTableLogList();

        int segmentTime = htableSegmentEntity.getHbtablesegmenttime();
        String currentTableName = htableSegmentEntity.getHbcurrenttablename();

        TableLogEntity currentTable = tableLogEntityList.stream()
                .filter(e -> e.getTablelogName().equals(currentTableName)).findFirst().get();

        Date createTime = new Date(currentTable.getTablelogCreatetime());

        XxlJobLogger.log(String.format("currenttableName: %s,createTime: %s,segmentTime :%s"
                , currentTableName, createTime.toString(), segmentTime));

        int day = getDatePoor(createTime, new Date());

        if (day >= segmentTime) {

            XxlJobLogger.log(String.format("is create,time difference: %s", day));

            return true;
        } else {

            XxlJobLogger.log(String.format("not create,time difference: %s", day));

            return false;
        }
    }

    private List<TableSegmentEntity> getTableSegmentInfo() throws Exception {
        String url = String.format("%s/api/dlapiservice/v1/datalake/tablesegmentinfo", dlapiUrl);

        String rstr = TMHttpClient.doAsyncGet(url, null);
        List<TableSegmentEntity> rlist = gson.fromJson(rstr, new TypeToken<List<TableSegmentEntity>>() {
        }.getType());

        return rlist;
    }

    private int getDatePoor(Date startDate, Date endDate) {
        long nm = 1000 * 60 * 60 * 24;

        long diff = endDate.getTime() - startDate.getTime();
        long day = diff / nm;

        return (int) day;

//        long nm = 1000 * 60;
//
//        long diff = endDate.getTime() - startDate.getTime();
//        long min = diff / nm;
//
//        return (int) min;

    }
}
