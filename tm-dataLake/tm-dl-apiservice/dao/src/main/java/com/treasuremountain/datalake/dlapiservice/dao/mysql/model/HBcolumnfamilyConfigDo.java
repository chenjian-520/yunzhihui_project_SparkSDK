package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBcolumnfamilyConfigDto;

import java.util.Date;

public class HBcolumnfamilyConfigDo implements BaseDo<HBcolumnfamilyConfigDto> {
    private String hbcolumnfamilyId;

    private String hbtableId;

    private String hbcolumnfamilyName;

    private Boolean hbcolumnfamilyIsenable;

    private String hbcolumnfamilyModifiedby;

    private Date hbcolumnfamilyModifieddt;

    private String hbcolumnfamilyCreateby;

    private Date hbcolumnfamilyCreatedt;

    public String getHbcolumnfamilyId() {
        return hbcolumnfamilyId;
    }

    public void setHbcolumnfamilyId(String hbcolumnfamilyId) {
        this.hbcolumnfamilyId = hbcolumnfamilyId == null ? null : hbcolumnfamilyId.trim();
    }

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId == null ? null : hbtableId.trim();
    }

    public String getHbcolumnfamilyName() {
        return hbcolumnfamilyName;
    }

    public void setHbcolumnfamilyName(String hbcolumnfamilyName) {
        this.hbcolumnfamilyName = hbcolumnfamilyName == null ? null : hbcolumnfamilyName.trim();
    }

    public Boolean getHbcolumnfamilyIsenable() {
        return hbcolumnfamilyIsenable;
    }

    public void setHbcolumnfamilyIsenable(Boolean hbcolumnfamilyIsenable) {
        this.hbcolumnfamilyIsenable = hbcolumnfamilyIsenable;
    }


    public String getHbcolumnfamilyModifiedby() {
        return hbcolumnfamilyModifiedby;
    }

    public void setHbcolumnfamilyModifiedby(String hbcolumnfamilyModifiedby) {
        this.hbcolumnfamilyModifiedby = hbcolumnfamilyModifiedby == null ? null : hbcolumnfamilyModifiedby.trim();
    }

    public Date getHbcolumnfamilyModifieddt() {
        return hbcolumnfamilyModifieddt;
    }

    public void setHbcolumnfamilyModifieddt(Date hbcolumnfamilyModifieddt) {
        this.hbcolumnfamilyModifieddt = hbcolumnfamilyModifieddt;
    }

    public String getHbcolumnfamilyCreateby() {
        return hbcolumnfamilyCreateby;
    }

    public void setHbcolumnfamilyCreateby(String hbcolumnfamilyCreateby) {
        this.hbcolumnfamilyCreateby = hbcolumnfamilyCreateby == null ? null : hbcolumnfamilyCreateby.trim();
    }

    public Date getHbcolumnfamilyCreatedt() {
        return hbcolumnfamilyCreatedt;
    }

    public void setHbcolumnfamilyCreatedt(Date hbcolumnfamilyCreatedt) {
        this.hbcolumnfamilyCreatedt = hbcolumnfamilyCreatedt;
    }

    @Override
    public HBcolumnfamilyConfigDto toData() {
        HBcolumnfamilyConfigDto dto = new HBcolumnfamilyConfigDto();
        dto.setHbcolumnfamilyId(this.hbcolumnfamilyId);
        dto.setHbtableId(this.hbtableId);
        dto.setHbcolumnfamilyName(this.hbcolumnfamilyName);
        dto.setHbcolumnfamilyIsenable(this.hbcolumnfamilyIsenable);
        dto.setHbcolumnfamilyModifiedby(this.hbcolumnfamilyModifiedby);
        dto.setHbcolumnfamilyModifieddt(this.hbcolumnfamilyModifieddt);
        dto.setHbcolumnfamilyCreateby(this.hbcolumnfamilyCreateby);
        dto.setHbcolumnfamilyCreatedt(this.hbcolumnfamilyCreatedt);
        return dto;
    }
}