package com.treasuremountain.machinecollector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by gerrywjzhao on 7/1/2017.
 */
public class BaseController {

    private final static Logger log = LoggerFactory.getLogger(BaseController.class);

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;

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
}
