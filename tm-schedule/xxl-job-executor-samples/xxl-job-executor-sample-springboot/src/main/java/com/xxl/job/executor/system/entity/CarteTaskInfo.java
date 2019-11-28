package com.xxl.job.executor.system.entity;

public class CarteTaskInfo {
    private boolean result;
    private String msg;
    private String resultcode;
    private CarteTaskDetailInfo data;

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

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public CarteTaskDetailInfo getData() {
        return data;
    }

    public void setData(CarteTaskDetailInfo data) {
        this.data = data;
    }
}
