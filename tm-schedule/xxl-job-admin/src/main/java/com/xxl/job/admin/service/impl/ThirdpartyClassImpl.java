package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.ThirdpartyClassDo;
import com.xxl.job.admin.dao.ThirdpartyClassDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThirdpartyClassImpl {
    private static Logger logger = LoggerFactory.getLogger(ExecutorChoiceImpl.class);

    @Resource
    private ThirdpartyClassDao thirdpartyClassDao;

    public List<Map<String, Object>> thirdpartyClassInfo() {
        List<Map<String, Object>> rlist = new ArrayList<>();

        List<ThirdpartyClassDo> doList = thirdpartyClassDao.selectAll();
        doList.forEach(d -> {
            Map<String, Object> rm = new HashMap<>();
            rm.put("thirdpartyclassName", d.getThirdpartyclassName());
            rm.put("thirdpartyclassClasspah", d.getThirdpartyclassClasspah());

            rlist.add(rm);
        });

        return rlist;
    }
}
