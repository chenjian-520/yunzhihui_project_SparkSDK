package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/4.
 * Company: Maxnerva
 * Project: polaris
 */
public class RestResult<T> {
    private boolean result = true;
    private String msg;
    private int resultcode = 200;
    private T data;

    public RestResult() {
    }

    public RestResult(String msg) {
        this.msg = msg;
    }

    public RestResult(boolean result, String msg, int resultcode) {
        this.result = result;
        this.msg = msg;
        this.resultcode = resultcode;
    }

    public RestResult(boolean result, String msg, T data) {
        this.result = result;
        this.msg = msg;
        this.resultcode = resultcode;
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResultcode() {
        return resultcode;
    }

    public void setResultcode(int resultcode) {
        this.resultcode = resultcode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
