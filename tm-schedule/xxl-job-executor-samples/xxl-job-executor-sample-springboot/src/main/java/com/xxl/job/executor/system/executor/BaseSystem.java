package com.xxl.job.executor.system.executor;

import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public abstract class BaseSystem {
    public abstract ReturnT<String> execute(Map<String, String> param, int logId);

    public abstract ReturnT<String> interrupt(Map<String, String> param, int logId);
}
