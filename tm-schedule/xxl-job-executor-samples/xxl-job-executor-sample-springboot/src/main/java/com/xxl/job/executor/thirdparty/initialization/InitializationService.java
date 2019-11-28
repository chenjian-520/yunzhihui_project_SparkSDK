package com.xxl.job.executor.thirdparty.initialization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.system.executor.SysCleanCarteCache;
import com.xxl.job.executor.thirdparty.DPCache;
import com.xxl.job.executor.thirdparty.ExecutorClassEntity;
import com.xxl.job.executor.thirdparty.executor.BaseThirdParty;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@Component
public class InitializationService {

    @Value("${xxl.job.admin.addresses}")
    private String adminUrl;

    @Value("${xxl.job.executor.code}")
    private String executorCode;

    @Value("${tmexec.http.timeout}")
    private int httptimeout;

    @Value("${tmexec.http.poolingmaxtotal}")
    private int poolingmaxtotal;

    @Value("${tm.management.url}")
    private String url;

    @Value("${tm.computation.url}")
    private String computationUrl;

    Gson gson = new Gson();

    public static java.util.Timer timer = new java.util.Timer(true);

    @PostConstruct
    public void init() throws IOReactorException, InterruptedException {

        DPCache.tmmanagementurl = url;
        DPCache.computationUrl = computationUrl;

        TMHttpClient.init(httptimeout, poolingmaxtotal);
        DPCache.executorCode = executorCode;
        DPCache.choiceInfo = getExecutorChoiceInfo();
        cacheExecClass();

        autoRefreshCache();
    }

    private List<Map<String, Object>> execGet(String url) throws InterruptedException {
        final boolean[] flg = {false};
        final List<Map<String, Object>>[] rlist = new List[1];

        CountDownLatch latch1 = new CountDownLatch(1);

        TMHttpClient.doGet(url, null, parm -> {
            String sr = parm.getResponseBody();

            if (parm.getStatusCode() != 200 && parm.getStatusCode() != 201) {
                XxlJobLogger.log(sr);
                flg[0] = false;
            } else {
                rlist[0] = gson.fromJson(sr, new TypeToken<List<Map<String, Object>>>() {
                }.getType());
                flg[0] = true;
            }

            latch1.countDown();
        });

        latch1.await();

        if (flg[0]) {
            return rlist[0];
        } else {
            return null;
        }
    }

    private List<Map<String, Object>> getExecutorClassInfo() throws InterruptedException {
        String infoUrl = adminUrl + "/executor/ClassInfo";
        return execGet(infoUrl);
    }

    private List<Map<String, Object>> getExecutorChoiceInfo() throws InterruptedException {
        String infoUrl = adminUrl + "/executor/choiceInfo";
        return execGet(infoUrl);
    }

    private void cacheExecClass() throws InterruptedException {
        List<Map<String, Object>> classInfo = getExecutorClassInfo();

        classInfo.forEach(d -> {
            try {
                String classPath = d.get("thirdpartyclassClasspah").toString();

                Class<?> aClass = Class.forName(classPath);

                ExecutorClassEntity entity = new ExecutorClassEntity();
                entity.setExecutorName(d.get("thirdpartyclassName").toString());
                entity.setExecutor((BaseThirdParty) aClass.newInstance());

                DPCache.execClass.add(entity);
            } catch (Exception e) {
                XxlJobLogger.log(e.toString());
            }
        });
    }

    public void autoRefreshCache() {
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    DPCache.choiceInfo = getExecutorChoiceInfo();
                } catch (Exception ex) {
                    XxlJobLogger.log(ex.toString());
                }
            }
        };
        timer.schedule(task, 0, 10 * 60 * 1000);
    }
}
