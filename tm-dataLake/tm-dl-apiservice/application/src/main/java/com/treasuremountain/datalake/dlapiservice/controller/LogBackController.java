package com.treasuremountain.datalake.dlapiservice.controller;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.treasuremountain.datalake.dlapiservice.common.message.HttpResponseMsg;
import com.treasuremountain.datalake.dlapiservice.config.webconfig.ApiVersion;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.ServerException;

@Slf4j
@RestController
@RequestMapping("/api/dlapiservice/{version}/")
public class LogBackController extends BaseController {


    @RequestMapping(value = "logback/loglevel/{packagename}/{loglevel}", consumes = "application/json",
            method = RequestMethod.PUT)
    @ApiVersion(1)
    public HttpResponseMsg addUser(@PathVariable("packagename") String packagename, @PathVariable("loglevel") String loglevel) throws ServerException {
        HttpResponseMsg<Boolean> restResult = new HttpResponseMsg();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            loggerContext.getLogger(packagename).setLevel(Level.valueOf(loglevel));
        } catch (Exception e) {
            log.error("动态修改日志级别出错，原因：" + e.getMessage(), e);
            restResult.setMsg("动态修改日志级别出错，原因：" + e.getMessage());
            restResult.setData(false);
            return restResult;
        }
        restResult.setMsg("成功设置log等级,现在的等级为：" + loggerContext.getLogger(packagename).getLevel());
        restResult.setData(true);
        return restResult;
    }

}
