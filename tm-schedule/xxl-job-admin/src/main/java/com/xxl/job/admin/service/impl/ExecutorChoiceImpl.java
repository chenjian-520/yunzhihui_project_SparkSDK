package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.ExecutorChoiceDo;
import com.xxl.job.admin.dao.ExecutorChoiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExecutorChoiceImpl {
    private static Logger logger = LoggerFactory.getLogger(ExecutorChoiceImpl.class);

    @Resource
    private ExecutorChoiceDao executorChoiceDao;

    public List<Map<String, Object>> executorChoiceInfo() {

        List<Map<String, Object>> rlist = new ArrayList<>();

        List<ExecutorChoiceDo> choiceDoList = executorChoiceDao.selectAll();
        choiceDoList.forEach(d -> {
            Map<String, Object> rm = new HashMap<>();
            rm.put("executorchoiceExecutor", d.getExecutorchoiceExecutor());
            rm.put("executorchoiceLocation", d.getExecutorchoiceLocation());
            rm.put("executorchoiceType", d.getExecutorchoiceType());
            rm.put("executorchoiceUri", d.getExecutorchoiceUri());
            rlist.add(rm);
        });

        return rlist;
    }
}
