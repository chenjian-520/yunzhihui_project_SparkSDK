package com.xxl.job.executor.thirdparty.executor.spark;

import com.google.gson.Gson;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.thirdparty.common.tmliaisons.Liaison;
import com.xxl.job.executor.thirdparty.common.tmliaisons.LiaisonResultDto;
import com.xxl.job.executor.thirdparty.common.tmliaisons.ScheduleInfoDto;
import com.xxl.job.executor.thirdparty.common.tmliaisons.TaskExcuteInfoDto;
import com.xxl.job.executor.thirdparty.executor.BaseThirdParty;
import com.xxl.job.executor.thirdparty.executor.kettle.KettleParm;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class SparkExecutor extends BaseThirdParty {

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

            XxlJobLogger.log(String.format("start spark,uri: %s,dataStr: %s", uri, dataStr));

            if (!startSpark(uri, logId, scheduleInfoDto)) {
                throw new Exception("start error");
            }
            while (true) {

                String status = checkStatus(uri, logId);
                if (status.equals("FINISHED") || status.equals("KILLED")) {
                    returnT.setCode(200);
                    break;
                } else if (status.equals("FAILED")) {
                    returnT.setCode(500);
                    break;
                }

                Thread.sleep(1000);
            }

        } catch (Exception ex) {
            returnT.setCode(500);
            returnT.setMsg(ex.toString());
        }

        return returnT;
    }

    @Override
    public ReturnT<String> interrupt(String taskId, String type, int logId) {

        ReturnT<String> returnT = new ReturnT<>();

        try {
            String uri = super.choiceURI("computation", type);

            uri = uri + "/api/common/v1/spark/controle/" + logId;

            LiaisonResultDto liaisonResultDto = liaison.getScheduleInfo(taskId);
            String dataStr = gson.toJson(liaisonResultDto.getData());
            ScheduleInfoDto scheduleInfoDto = gson.fromJson(dataStr, ScheduleInfoDto.class);

            if (!killSpark(uri, logId, scheduleInfoDto)) {
                throw new Exception("stop error");
            }
            returnT.setCode(200);
        } catch (Exception ex) {
            returnT.setCode(500);
            returnT.setMsg(ex.toString());
        }

        return returnT;
    }

    private boolean startSpark(String uri, int xxscheduleid, ScheduleInfoDto scheduleInfoDto) throws Exception {

        String cpurl = uri + "/api/common/v1/spark/controle/" + xxscheduleid;

        TaskExcuteInfoDto taskExcuteInfoDto = scheduleInfoDto.getTaskExcuteInfoDto();

        SparkParm sparm = new SparkParm();
        sparm.setId(taskExcuteInfoDto.getId());
        sparm.setControlcode("start");
        sparm.setQueue(taskExcuteInfoDto.getQueue());
        sparm.setMaxExecutors(taskExcuteInfoDto.getMaxExecutors());
        sparm.setMiExecutors(taskExcuteInfoDto.getMinExecutors());
        sparm.setExecutoridleTimeout(taskExcuteInfoDto.getExecutoridleTimeout());
        sparm.setSchedulerbacklogTimeout(taskExcuteInfoDto.getSchedulerbacklogTimeout());
        sparm.setBusinessid(taskExcuteInfoDto.getBusinessid());
        sparm.setParams(scheduleInfoDto.getScheduleParams());
        //2019/8/13
        sparm.setDriverMemory(taskExcuteInfoDto.getDriverMemory());
        sparm.setExecutorMemory(taskExcuteInfoDto.getExecutorMemory());
        sparm.setDependInfo(taskExcuteInfoDto.getDependInfo());
//        sparm.setDependItem(taskExcuteInfoDto.getDependItem());
//        sparm.setDependUser(taskExcuteInfoDto.getDependUser());

        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        String strkparm = gson.toJson(sparm);

        XxlJobLogger.log(String.format("startspark request url:%s,parm:%s", cpurl, strkparm));

        String rstr = TMHttpClient.doAsyncPost(cpurl, header, strkparm);

        XxlJobLogger.log(String.format("start result:%s", rstr));

        LiaisonResultDto dto = gson.fromJson(rstr, LiaisonResultDto.class);
        return dto.isResult();
    }

    private String checkStatus(String uri, int xxscheduleid) throws Exception {

        String cpurl = uri + "/api/common/v1/spark/controle/runstate/xxid/" + xxscheduleid;

        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        String rstr = TMHttpClient.doAsyncGet(cpurl, header);

        XxlJobLogger.log(String.format("checkStatus result:%s", rstr));

        LiaisonResultDto dto = gson.fromJson(rstr, LiaisonResultDto.class);

        return dto.getData().toString();
    }

    private boolean killSpark(String uri, int xxscheduleid, ScheduleInfoDto scheduleInfoDto) throws Exception {

        String cpurl = uri + "/api/common/v1/spark/controle/" + xxscheduleid;

        TaskExcuteInfoDto taskExcuteInfoDto = scheduleInfoDto.getTaskExcuteInfoDto();

        KettleParm kparm = new KettleParm();
        kparm.setId(taskExcuteInfoDto.getId());
        kparm.setControlcode("stop");

        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        String rstr = TMHttpClient.doAsyncPost(cpurl, header, gson.toJson(kparm));

        XxlJobLogger.log(String.format("killSpark result:%s", rstr));

        LiaisonResultDto dto = gson.fromJson(rstr, LiaisonResultDto.class);
        return dto.isResult();
    }
}
