package com.xxl.job.executor.service.jobhandler;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.system.SysExecExecutorClassEntity;
import com.xxl.job.executor.system.executor.BaseSystem;
import com.xxl.job.executor.system.initialization.SysInitializationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@JobHandler(value = "SystemHandler")
@Component
public class SystemHandler extends IJobHandler {

    Gson gson = new Gson();

    @Override
    public ReturnT<String> execute(String param, int logId) throws Exception {
        try {

            XxlJobLogger.log(String.format("parm:%s logid:%s", param, logId));

            ReturnT<String> returnT = FAIL;

            Map<String, String> parmMap = gson.fromJson(param, new TypeToken<Map<String, String>>() {
            }.getType());

            String sysExecClass = parmMap.get("sysExecClass");

            Class<?> aClass = Class.forName(sysExecClass);

            Optional<SysExecExecutorClassEntity> oClassEntity = SysInitializationService.sysExecExecutorCache.stream()
                    .filter(d -> d.getExecutorClassName().equals(sysExecClass)).findFirst();

            SysExecExecutorClassEntity classEntity;

            if (oClassEntity.isPresent()) {
                classEntity = oClassEntity.get();
            } else {
                classEntity = new SysExecExecutorClassEntity();
                classEntity.setExecutorClassName(sysExecClass);
                classEntity.setExecutor((BaseSystem) aClass.newInstance());

                SysInitializationService.sysExecExecutorCache.add(classEntity);
            }

            returnT = classEntity.getExecutor().execute(parmMap, logId);

            if (returnT.getCode() == 408) {
                XxlJobLogger.log("timeout or kill event");
                returnT = classEntity.getExecutor().interrupt(parmMap, logId);
            }

            return returnT;

        } catch (Exception ex) {
            XxlJobLogger.log(ex);
            return FAIL;
        }
    }
}
