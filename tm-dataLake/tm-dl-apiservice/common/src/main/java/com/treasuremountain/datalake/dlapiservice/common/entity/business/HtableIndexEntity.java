package com.treasuremountain.datalake.dlapiservice.common.entity.business;

public class HtableIndexEntity {
    private String hbtableId;

    private Boolean hbtableIstwoLevelIndex;

    private int hbsegmenttime;

    private int hbretentiontime;

    private String hbcurrentindexname;

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId;
    }

    public boolean isHbtableIstwoLevelIndex() {
        return hbtableIstwoLevelIndex;
    }

    public void setHbtableIstwoLevelIndex(Boolean hbtableIstwoLevelIndex) {
        this.hbtableIstwoLevelIndex = hbtableIstwoLevelIndex;
    }

    public int getHbsegmenttime() {
        return hbsegmenttime;
    }

    public void setHbsegmenttime(int hbsegmenttime) {
        this.hbsegmenttime = hbsegmenttime;
    }

    public int getHbretentiontime() {
        return hbretentiontime;
    }

    public void setHbretentiontime(int hbretentiontime) {
        this.hbretentiontime = hbretentiontime;
    }

    public String getHbcurrentindexname() {
        return hbcurrentindexname;
    }

    public void setHbcurrentindexname(String hbcurrentindexname) {
        this.hbcurrentindexname = hbcurrentindexname;
    }
}
