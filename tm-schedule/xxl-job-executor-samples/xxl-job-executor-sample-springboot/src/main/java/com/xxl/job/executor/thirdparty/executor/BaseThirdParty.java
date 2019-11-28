package com.xxl.job.executor.thirdparty.executor;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.executor.thirdparty.DPCache;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public abstract class BaseThirdParty {

    protected String choiceURI(String location, String type) {
        List<String> uriList = new ArrayList<>();
        DPCache.choiceInfo.stream().filter(d -> d.get("executorchoiceLocation").equals(location)
                && d.get("executorchoiceType").equals(type)
                && d.get("executorchoiceExecutor").equals(DPCache.executorCode)).forEach(f -> {

            uriList.add(f.get("executorchoiceUri").toString());
        });

        int uc = uriList.size();

        Random ra = new Random();
        int ci = ra.nextInt(uc);

        return uriList.get(ci);
    }

    public abstract ReturnT<String> execute(String taskId, String type, int logId);

    public abstract ReturnT<String> interrupt(String taskId, String type, int logId);
}
