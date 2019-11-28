package com.xxl.job.executor.service.jobhandler;

import com.google.gson.Gson;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.thirdparty.DPCache;
import com.xxl.job.executor.thirdparty.ExecParmEntity;
import com.xxl.job.executor.thirdparty.executor.BaseThirdParty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@JobHandler(value = "InvokeThirdPartyHandler")
@Component
public class InvokeThirdPartyHandler extends IJobHandler {

    Gson gson = new Gson();
    BaseThirdParty thirdPartyExe = null;
    String type = "";
    String scheduleid = "";

    @Override
    public ReturnT<String> execute(String param, int logId) {
        try {

            XxlJobLogger.log(String.format("parm:%s logid:%s", param, logId));

            final ReturnT<String>[] returnT = new ReturnT[1];
            returnT[0] = FAIL;

            ExecParmEntity parmEntity = gson.fromJson(param, ExecParmEntity.class);
            this.type = parmEntity.getType();
            this.scheduleid = parmEntity.getScheduleid();

            DPCache.execClass.stream().filter(d -> d.getExecutorName().equals(type)).findFirst().map(f -> {
                this.thirdPartyExe = f.getExecutor();
                returnT[0] = this.thirdPartyExe.execute(this.scheduleid, this.type, logId);
                return true;
            });

            if (returnT[0].getCode() == 408) {
                XxlJobLogger.log("timeout or kill event");
                returnT[0] = thirdPartyExe.interrupt(this.scheduleid, this.type, logId);
            }

            return returnT[0];

        } catch (Exception ex) {
            XxlJobLogger.log(ex);
            return FAIL;
        }
    }
}
