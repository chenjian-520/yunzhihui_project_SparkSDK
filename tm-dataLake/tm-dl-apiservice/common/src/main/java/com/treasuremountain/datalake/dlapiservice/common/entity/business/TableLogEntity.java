package com.treasuremountain.datalake.dlapiservice.common.entity.business;

import java.util.Date;

public class TableLogEntity {

    private String tablelogId;

    private String hbtableId;

    private String tablelogName;

    private long tablelogCreatetime;

    public String getTablelogId() {
        return tablelogId;
    }

    public void setTablelogId(String tablelogId) {
        this.tablelogId = tablelogId;
    }

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId;
    }

    public String getTablelogName() {
        return tablelogName;
    }

    public void setTablelogName(String tablelogName) {
        this.tablelogName = tablelogName;
    }

    public long getTablelogCreatetime() {
        return tablelogCreatetime;
    }

    public void setTablelogCreatetime(long tablelogCreatetime) {
        this.tablelogCreatetime = tablelogCreatetime;
    }
}
