package com.treasuremountain.datalake.dlapiservice.controller;

import com.alibaba.fastjson.JSON;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest.SecurityUser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.treasuremountain.datalake.dlapiservice.service.initialization.InitializationService.maxHttpClientUtils;

/**
 * Created by gerrywjzhao on 7/1/2017.
 */
@Component
public class BaseController {

    private final static Logger log = LoggerFactory.getLogger(BaseController.class);

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;

    @Value("${management.permision-service}")
    private String managementUrl;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
    }

    public String getRsquestInfoStr() {
        try {
            String info = "RequestURL:%s RemoteAddr:%s RemoteHost:%s RemotePort:%s LocalAddr:%s LocalName:%s ";

            info = String.format(info, request.getRequestURL(), request.getRemoteAddr(),
                    request.getRemoteHost(), request.getRemotePort(),
                    request.getLocalAddr(), request.getLocalName());

            return info;
        } catch (Exception ex) {
            log.error(ex.toString());
            return "";
        }
    }

    public String getCurrentUser() throws IOException {
        try {
            String currentUserId = null;
            String userId = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
            HttpGet hg = new HttpGet(managementUrl + userId);
            hg.setHeader("Content-Type", "application/json");
            HttpResponse httpResponse = maxHttpClientUtils.execution(hg, null);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                String resultJson = EntityUtils.toString(entity);
                currentUserId = JSON.parseObject(resultJson).getJSONObject("data").getString("permissionUsername");
            } else {
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                log.error("获取permission userid异常" + result);
            }
            if (currentUserId == null) {
                throw new SecurityException("permission User Not Found.");
            }
            return currentUserId;
        } catch (SecurityException e) {
            throw e;
        }
    }
}
