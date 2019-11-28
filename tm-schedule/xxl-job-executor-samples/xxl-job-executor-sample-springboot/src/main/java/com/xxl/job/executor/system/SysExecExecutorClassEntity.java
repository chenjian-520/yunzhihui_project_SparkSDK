package com.xxl.job.executor.system;

import com.xxl.job.executor.system.executor.BaseSystem;

public class SysExecExecutorClassEntity {
    private String executorClassName;

    private BaseSystem executor;

    public String getExecutorClassName() {
        return executorClassName;
    }

    public void setExecutorClassName(String executorClassName) {
        this.executorClassName = executorClassName;
    }

    public BaseSystem getExecutor() {
        return executor;
    }

    public void setExecutor(BaseSystem executor) {
        this.executor = executor;
    }
}
