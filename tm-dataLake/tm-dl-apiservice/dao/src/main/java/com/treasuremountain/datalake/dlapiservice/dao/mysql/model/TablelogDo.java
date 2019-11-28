package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.TableLogDto;

import java.util.Date;

public class TablelogDo implements BaseDo<TableLogDto>{
    private String tablelogId;

    private String hbtableId;

    private String tablelogName;

    private Date tablelogCreatetime;

    public String getTablelogId() {
        return tablelogId;
    }

    public void setTablelogId(String tablelogId) {
        this.tablelogId = tablelogId == null ? null : tablelogId.trim();
    }

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId == null ? null : hbtableId.trim();
    }

    public String getTablelogName() {
        return tablelogName;
    }

    public void setTablelogName(String tablelogName) {
        this.tablelogName = tablelogName == null ? null : tablelogName.trim();
    }

    public Date getTablelogCreatetime() {
        return tablelogCreatetime;
    }

    public void setTablelogCreatetime(Date tablelogCreatetime) {
        this.tablelogCreatetime = tablelogCreatetime;
    }

    @Override
    public TableLogDto toData() {

        TableLogDto dto = new TableLogDto();
        dto.setHbtableId(hbtableId);
        dto.setTablelogId(tablelogId);
        dto.setTablelogName(tablelogName);
        dto.setTablelogCreatetime(tablelogCreatetime);

        return dto;
    }
}