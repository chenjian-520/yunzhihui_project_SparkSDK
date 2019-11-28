package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex.IndexlogDto;

import java.util.Date;

public class IndexlogDo implements BaseDo<IndexlogDto> {
    private String indexlogId;

    private String hbtableId;

    private String indexlogName;

    private Date indexlogCreatetime;

    public String getIndexlogId() {
        return indexlogId;
    }

    public void setIndexlogId(String indexlogId) {
        this.indexlogId = indexlogId == null ? null : indexlogId.trim();
    }

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId == null ? null : hbtableId.trim();
    }

    public String getIndexlogName() {
        return indexlogName;
    }

    public void setIndexlogName(String indexlogName) {
        this.indexlogName = indexlogName == null ? null : indexlogName.trim();
    }

    public Date getIndexlogCreatetime() {
        return indexlogCreatetime;
    }

    public void setIndexlogCreatetime(Date indexlogCreatetime) {
        this.indexlogCreatetime = indexlogCreatetime;
    }

    @Override
    public IndexlogDto toData() {
        IndexlogDto dto = new IndexlogDto();
        dto.setIndexlogId(this.indexlogId);
        dto.setHbtableId(this.hbtableId);
        dto.setIndexlogName(this.indexlogName);
        dto.setIndexlogCreatetime(this.indexlogCreatetime);
        return dto;
    }
}