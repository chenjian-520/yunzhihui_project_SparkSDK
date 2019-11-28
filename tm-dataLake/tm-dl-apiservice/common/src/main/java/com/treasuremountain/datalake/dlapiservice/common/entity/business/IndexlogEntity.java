package com.treasuremountain.datalake.dlapiservice.common.entity.business;

import java.util.Date;

public class IndexlogEntity {
    private String indexlogId;

    private String hbtableId;

    private String indexlogName;

    private long indexlogCreatetime;

    public String getIndexlogId() {
        return indexlogId;
    }

    public void setIndexlogId(String indexlogId) {
        this.indexlogId = indexlogId;
    }

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId;
    }

    public String getIndexlogName() {
        return indexlogName;
    }

    public void setIndexlogName(String indexlogName) {
        this.indexlogName = indexlogName;
    }

    public long getIndexlogCreatetime() {
        return indexlogCreatetime;
    }

    public void setIndexlogCreatetime(long indexlogCreatetime) {
        this.indexlogCreatetime = indexlogCreatetime;
    }
}
