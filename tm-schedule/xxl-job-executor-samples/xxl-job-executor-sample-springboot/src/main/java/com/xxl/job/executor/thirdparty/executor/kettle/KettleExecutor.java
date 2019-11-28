package com.xxl.job.executor.thirdparty.executor.kettle;

import com.google.gson.Gson;
import com.sun.org.apache.xalan.internal.xsltc.dom.SimpleResultTreeImpl;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.thirdparty.common.tmliaisons.Liaison;
import com.xxl.job.executor.thirdparty.common.tmliaisons.LiaisonResultDto;
import com.xxl.job.executor.thirdparty.common.tmliaisons.ScheduleInfoDto;
import com.xxl.job.executor.thirdparty.common.tmliaisons.TaskExcuteInfoDto;
import com.xxl.job.executor.thirdparty.executor.BaseThirdParty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

@Component
public class KettleExecutor extends BaseThirdParty {

    Gson gson = new Gson();

    private Liaison liaison = new Liaison();

    @Override
    public ReturnT<String> execute(String taskId, String type, int logId) {

        ReturnT<String> returnT = new ReturnT<>();

        try {
            String uri = super.choiceURI("computation", type);

            LiaisonResultDto liaisonResultDto = liaison.getScheduleInfo(taskId);
            String dataStr = gson.toJson(liaisonResultDto.getData());
            ScheduleInfoDto scheduleInfoDto = gson.fromJson(dataStr, ScheduleInfoDto.class);

            XxlJobLogger.log(String.format("start kettle,uri: %s,dataStr: %s", uri, dataStr));

            if (!startKettle(uri, logId, scheduleInfoDto)) {
                throw new Exception("start error");
            }
            while (true) {

                String status = checkStatus(uri, logId);
                status = StringUtils.deleteWhitespace(status);

                if (status.equals("Finished") || status.equals("STOPPED")) {
                    returnT.setCode(200);
                    break;
                } else if (status.equals("Finished(witherrors)") || status.equals("Halting")) {
                    returnT.setCode(500);
                    break;
                }

                Thread.sleep(1000);
            }
        } catch (Exception ex) {
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
    public ReturnT<String> interrupt(String taskId, String type, int logId) {

        ReturnT<String> returnT = new ReturnT<>();

        try {
            String uri = super.choiceURI("computation", type);

            uri = uri + "/api/common/v1/kettle/control/" + logId;

            LiaisonResultDto liaisonResultDto = liaison.getScheduleInfo(taskId);
            String dataStr = gson.toJson(liaisonResultDto.getData());
            ScheduleInfoDto scheduleInfoDto = gson.fromJson(dataStr, ScheduleInfoDto.class);

            if (!killKettle(uri, logId, scheduleInfoDto)) {
                throw new Exception("stop error");
            }
            returnT.setCode(200);
        } catch (Exception ex) {
            returnT.setCode(500);
            returnT.setMsg(ex.toString());
        }

        return returnT;
    }

    private boolean startKettle(String uri, int xxscheduleid, ScheduleInfoDto scheduleInfoDto) throws Exception {

        String cpurl = uri + "/api/common/v1/kettle/control/" + xxscheduleid;

        TaskExcuteInfoDto taskExcuteInfoDto = scheduleInfoDto.getTaskExcuteInfoDto();

        KettleParm kparm = new KettleParm();
        kparm.setId(taskExcuteInfoDto.getId());
        kparm.setControlcode("start");
        kparm.setClusterposition(taskExcuteInfoDto.getClusterPosition());
        kparm.setExcutemodel(taskExcuteInfoDto.getExcuteModel());
        kparm.setLogLevel(taskExcuteInfoDto.getLogLevel());
        kparm.setBusinessid(taskExcuteInfoDto.getBusinessid());
        kparm.setParams(scheduleInfoDto.getScheduleParams());

        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        String strkparm = gson.toJson(kparm);

        XxlJobLogger.log(String.format("startKettle request url:%s,parm:%s", cpurl, strkparm));

        String rstr = TMHttpClient.doAsyncPost(cpurl, header, strkparm);

        XxlJobLogger.log(String.format("start result:%s", rstr));

        LiaisonResultDto dto = gson.fromJson(rstr, LiaisonResultDto.class);
        return dto.isResult();

    }

    private String checkStatus(String uri, int xxscheduleid) throws Exception {

        String cpurl = uri + "/api/common/v1/kettle/status/xxid/" + xxscheduleid;

        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        String rstr = TMHttpClient.doAsyncGet(cpurl, header);

        XxlJobLogger.log(String.format("checkStatus result:%s", rstr));

        LiaisonResultDto dto = gson.fromJson(rstr, LiaisonResultDto.class);

        return dto.getData().toString();

    }

    private boolean killKettle(String uri, int xxscheduleid, ScheduleInfoDto scheduleInfoDto) throws Exception {

//        String cpurl = uri + "/api/common/v1/kettle/control/" + xxscheduleid;
        String cpurl = uri;
        TaskExcuteInfoDto taskExcuteInfoDto = scheduleInfoDto.getTaskExcuteInfoDto();

        KettleParm kparm = new KettleParm();
        kparm.setId(taskExcuteInfoDto.getId());
        kparm.setControlcode("stop");

        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        String strkparm = gson.toJson(kparm);

        XxlJobLogger.log(String.format("killKettle request url:%s,parm:%s", cpurl, strkparm));

        String rstr = TMHttpClient.doAsyncPost(cpurl, header, strkparm);

        XxlJobLogger.log(String.format("killKettle result:%s", rstr));

        LiaisonResultDto dto = gson.fromJson(rstr, LiaisonResultDto.class);
        return dto.isResult();
    }
}
