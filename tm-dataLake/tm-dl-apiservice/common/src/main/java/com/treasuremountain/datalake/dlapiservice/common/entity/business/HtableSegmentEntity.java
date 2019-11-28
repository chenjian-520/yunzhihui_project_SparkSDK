package com.treasuremountain.datalake.dlapiservice.common.entity.business;


public class HtableSegmentEntity {
    private String hbtableId;

    private Boolean hbtableIstablesegment;

    private int hbtablesegmenttime;

    private int hbtableretentiontime;

    private String hbcurrenttablename;

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId;
    }

    public boolean isHbtableIstablesegment() {
        return hbtableIstablesegment;
    }

    public void setHbtableIstablesegment(Boolean hbtableIstablesegment) {
        this.hbtableIstablesegment = hbtableIstablesegment;
    }

    public int getHbtablesegmenttime() {
        return hbtablesegmenttime;
    }

    public void setHbtablesegmenttime(int hbtablesegmenttime) {
        this.hbtablesegmenttime = hbtablesegmenttime;
    }

    public int getHbtableretentiontime() {
        return hbtableretentiontime;
    }

    public void setHbtableretentiontime(int hbtableretentiontime) {
        this.hbtableretentiontime = hbtableretentiontime;
    }

    public String getHbcurrenttablename() {
        return hbcurrenttablename;
    }

    public void setHbcurrenttablename(String hbcurrenttablename) {
        this.hbcurrenttablename = hbcurrenttablename;
    }
}
