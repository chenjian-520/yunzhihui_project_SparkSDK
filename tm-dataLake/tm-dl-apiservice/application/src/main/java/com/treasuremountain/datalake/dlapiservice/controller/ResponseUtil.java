package com.treasuremountain.datalake.dlapiservice.controller;

import com.treasuremountain.datalake.dlapiservice.common.message.HttpResponseMsg;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by gerryzhao on 11/2/2018.
 */
public class ResponseUtil {

    public static HttpResponseMsg ok(HttpServletResponse response, String strMsg, Object object) {
        return makeResult(response, 200, strMsg, object);
    }

    public static HttpResponseMsg created(HttpServletResponse response, String strMsg, Object object) {
        return makeResult(response, 201, strMsg, object);
    }

    public static HttpResponseMsg deleted(HttpServletResponse response, String strMsg, Object object) {
        return makeResult(response, 204, strMsg, object);
    }

    public static HttpResponseMsg badRequest(HttpServletResponse response, String strMsg, Object object) {
        return makeResult(response, 400, strMsg, object);
    }

    public static HttpResponseMsg notFound(HttpServletResponse response, String strMsg, Object object) {
        return makeResult(response, 404, strMsg, object);
    }

    public static HttpResponseMsg internalServerError(HttpServletResponse response, String strMsg, Object object) {
        return makeResult(response, 500, strMsg, object);
    }

    private static HttpResponseMsg makeResult(HttpServletResponse response, int code, String strMsg, Object object) {
        response.setStatus(code);
        HttpResponseMsg msg = new HttpResponseMsg();
        msg.setMsg(strMsg);
        msg.setData(object);
        return msg;
    }
}
