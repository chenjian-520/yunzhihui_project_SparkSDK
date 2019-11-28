package com.xxl.job.executor.thirdparty;

import com.xxl.job.executor.thirdparty.executor.BaseThirdParty;

public class ExecutorClassEntity {
    private String executorName;
    private BaseThirdParty executor;

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public BaseThirdParty getExecutor() {
        return executor;
    }

    public void setExecutor(BaseThirdParty executor) {
        this.executor = executor;
    }
}
