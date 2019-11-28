package com.treasuremountain.machinecollector.common.message;

/**
 * Created by gerryzhao on 11/2/2018.
 */
public class HttpResponseMsg<T> {
    private String msg;
    private  T data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
