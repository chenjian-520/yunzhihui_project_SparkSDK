package com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex;

import java.util.Date;

public class IndexlogDto {
    private String indexlogId;

    private String hbtableId;

    private String indexlogName;

    private Date indexlogCreatetime;

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

    public Date getIndexlogCreatetime() {
        return indexlogCreatetime;
    }

    public void setIndexlogCreatetime(Date indexlogCreatetime) {
        this.indexlogCreatetime = indexlogCreatetime;
    }
}
