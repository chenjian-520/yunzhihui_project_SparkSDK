package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBcolumnConfigDto;

import java.util.Date;

public class HBcolumnConfigDo implements BaseDo<HBcolumnConfigDto> {
    private String hbcolumnId;

    private String hbcolumnfamilyId;

    private String hbcolumnName;

    private Boolean hbcolumnIsenable;

    private Boolean hbcolumnIsindex;

    private String hbcolumnDesc;

    private String hbcolumnModifiedby;

    private Date hbcolumnModifieddt;

    private String hbcolumnCreateby;

    private Date hbcolumnCreatedt;

    public String getHbcolumnType() {
        return hbcolumnType;
    }

    public void setHbcolumnType(String hbcolumnType) {
        this.hbcolumnType = hbcolumnType;
    }

    private String hbcolumnType;


    public String getHbcolumnId() {
        return hbcolumnId;
    }

    public void setHbcolumnId(String hbcolumnId) {
        this.hbcolumnId = hbcolumnId == null ? null : hbcolumnId.trim();
    }

    public String getHbcolumnfamilyId() {
        return hbcolumnfamilyId;
    }

    public void setHbcolumnfamilyId(String hbcolumnfamilyId) {
        this.hbcolumnfamilyId = hbcolumnfamilyId == null ? null : hbcolumnfamilyId.trim();
    }

    public String getHbcolumnName() {
        return hbcolumnName;
    }

    public void setHbcolumnName(String hbcolumnName) {
        this.hbcolumnName = hbcolumnName == null ? null : hbcolumnName.trim();
    }

    public Boolean getHbcolumnIsenable() {
        return hbcolumnIsenable;
    }

    public void setHbcolumnIsenable(Boolean hbcolumnIsenable) {
        this.hbcolumnIsenable = hbcolumnIsenable;
    }

    public Boolean getHbcolumnIsindex() {
        return hbcolumnIsindex;
    }

    public void setHbcolumnIsindex(Boolean hbcolumnIsindex) {
        this.hbcolumnIsindex = hbcolumnIsindex;
    }

    public String gethbcolumnDesc() {
        return hbcolumnDesc;
    }

    public void sethbcolumnDesc(String hbcolumnDesc) {
        this.hbcolumnDesc = hbcolumnDesc == null ? null : hbcolumnDesc.trim();
    }


    public String getHbcolumnModifiedby() {
        return hbcolumnModifiedby;
    }

    public void setHbcolumnModifiedby(String hbcolumnModifiedby) {
        this.hbcolumnModifiedby = hbcolumnModifiedby == null ? null : hbcolumnModifiedby.trim();
    }

    public Date getHbcolumnModifieddt() {
        return hbcolumnModifieddt;
    }

    public void setHbcolumnModifieddt(Date hbcolumnModifieddt) {
        this.hbcolumnModifieddt = hbcolumnModifieddt;
    }

    public String getHbcolumnCreateby() {
        return hbcolumnCreateby;
    }

    public void setHbcolumnCreateby(String hbcolumnCreateby) {
        this.hbcolumnCreateby = hbcolumnCreateby == null ? null : hbcolumnCreateby.trim();
    }

    public Date getHbcolumnCreatedt() {
        return hbcolumnCreatedt;
    }

    public void setHbcolumnCreatedt(Date hbcolumnCreatedt) {
        this.hbcolumnCreatedt = hbcolumnCreatedt;
    }

    @Override
    public HBcolumnConfigDto toData() {
        HBcolumnConfigDto dto = new HBcolumnConfigDto();
        dto.setHbcolumnId(this.hbcolumnId);
        dto.setHbcolumnfamilyId(this.hbcolumnfamilyId);
        dto.setHbcolumnName(this.hbcolumnName);
        dto.setHbcolumnIsenable(this.hbcolumnIsenable);
        dto.setHbcolumnIsindex(this.hbcolumnIsindex);
        dto.setHbcolumnDesc(this.hbcolumnDesc);
        dto.setHbcolumnModifiedby(this.hbcolumnModifiedby);
        dto.setHbcolumnModifieddt(this.hbcolumnModifieddt);
        dto.setHbcolumnCreateby(this.hbcolumnCreateby);
        dto.setHbcolumnCreatedt(this.hbcolumnCreatedt);
        dto.setHbcolumnType(this.hbcolumnType);
        return dto;
    }
}