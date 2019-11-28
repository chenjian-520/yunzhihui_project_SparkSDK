package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermessionLimit;
import com.xxl.job.admin.service.impl.ExecutorChoiceImpl;
import com.xxl.job.admin.service.impl.ThirdpartyClassImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/executor")
public class ExecutorController {
    @Resource
    private ExecutorChoiceImpl executorChoice;

    @Resource
    private ThirdpartyClassImpl thirdpartyClass;

    @RequestMapping("/choiceInfo")
    @ResponseBody
    @PermessionLimit(limit = false)
    public List<Map<String, Object>> executorChoiceInfo() {
        List<Map<String, Object>> rml = executorChoice.executorChoiceInfo();
        return rml;
    }

    //http://localhost:8080/xxl-job-admin/executor/choiceInfo


    @RequestMapping("/ClassInfo")
    @ResponseBody
    @PermessionLimit(limit = false)
    public List<Map<String, Object>> thirdpartyClassInfo() {
        List<Map<String, Object>> rml = thirdpartyClass.thirdpartyClassInfo();
        return rml;
    }
}
