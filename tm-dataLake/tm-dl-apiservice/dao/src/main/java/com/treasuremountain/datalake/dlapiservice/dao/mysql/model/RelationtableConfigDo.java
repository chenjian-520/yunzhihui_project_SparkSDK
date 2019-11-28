package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.RelationtableConfigDto;

import java.util.Date;

public class RelationtableConfigDo implements BaseDo<RelationtableConfigDto> {
    private String relationtableId;

    private String businessId;
    //不这是通过leftJoin查询用的
    private String businessName;

    private String hbtableId;
    //不这是通过leftJoin查询用的
    private String hbtableName;

    private String msgkey;

    private String hbcolumnfamilyId;

    private String hbcolumnfamilyName;

    private String hbcolumnId;

    private String hbcolumnName;

    public String getHbcolumnType() {
        return hbcolumnType;
    }

    public void setHbcolumnType(String hbcolumnType) {
        this.hbcolumnType = hbcolumnType;
    }

    private String hbcolumnType;

    private Boolean relationtableIsenable;

    private String relationtableModifiedby;

    private Date relationtableModifieddt;

    private String relationtableCreateby;

    private Date relationtableCreatedt;

    public String getRelationtableId() {
        return relationtableId;
    }

    public void setRelationtableId(String relationtableId) {
        this.relationtableId = relationtableId == null ? null : relationtableId.trim();
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName == null ? null : businessName.trim();
    }

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId == null ? null : hbtableId.trim();
    }

    public String getHbtableName() {
        return hbtableName;
    }

    public void setHbtableName(String hbtableName) {
        this.hbtableName = hbtableName == null ? null : hbtableName.trim();
    }

    public String getMsgkey() {
        return msgkey;
    }

    public void setMsgkey(String msgkey) {
        this.msgkey = msgkey == null ? null : msgkey.trim();
    }

    public String getHbcolumnfamilyId() {
        return hbcolumnfamilyId;
    }

    public void setHbcolumnfamilyId(String hbcolumnfamilyId) {
        this.hbcolumnfamilyId = hbcolumnfamilyId;
    }

    public String getHbcolumnfamilyName() {
        return hbcolumnfamilyName;
    }

    public void setHbcolumnfamilyName(String hbcolumnfamilyName) {
        this.hbcolumnfamilyName = hbcolumnfamilyName;
    }

    public String getHbcolumnId() {
        return hbcolumnId;
    }

    public void setHbcolumnId(String hbcolumnId) {
        this.hbcolumnId = hbcolumnId == null ? null : hbcolumnId.trim();
    }

    public String getHbcolumnName() {
        return hbcolumnName;
    }

    public void setHbcolumnName(String hbcolumnName) {
        this.hbcolumnName = hbcolumnName == null ? null : hbcolumnName.trim();
    }

    public Boolean getRelationtableIsenable() {
        return relationtableIsenable;
    }

    public void setRelationtableIsenable(Boolean relationtableIsenable) {
        this.relationtableIsenable = relationtableIsenable;
    }

    public String getRelationtableModifiedby() {
        return relationtableModifiedby;
    }

    public void setRelationtableModifiedby(String relationtableModifiedby) {
        this.relationtableModifiedby = relationtableModifiedby == null ? null : relationtableModifiedby.trim();
    }

    public Date getRelationtableModifieddt() {
        return relationtableModifieddt;
    }

    public void setRelationtableModifieddt(Date relationtableModifieddt) {
        this.relationtableModifieddt = relationtableModifieddt;
    }

    public String getRelationtableCreateby() {
        return relationtableCreateby;
    }

    public void setRelationtableCreateby(String relationtableCreateby) {
        this.relationtableCreateby = relationtableCreateby == null ? null : relationtableCreateby.trim();
    }

    public Date getRelationtableCreatedt() {
        return relationtableCreatedt;
    }

    public void setRelationtableCreatedt(Date relationtableCreatedt) {
        this.relationtableCreatedt = relationtableCreatedt;
    }

    @Override
    public RelationtableConfigDto toData() {
        RelationtableConfigDto dto = new RelationtableConfigDto();
        dto.setRelationtableId(this.relationtableId);
        dto.setBusinessId(this.businessId);
        dto.setHbtableId(this.hbtableId);
        dto.setHbtableName(this.getHbtableName());
        dto.setBusinessName(this.getBusinessName());
        dto.setMsgkey(this.msgkey);
        dto.setHbcolumnfamilyId(this.hbcolumnfamilyId);
        dto.setHbcolumnfamilyName(this.getHbcolumnfamilyName());
        dto.setHbcolumnId(this.getHbcolumnId());
        dto.setHbcolumnName(this.getHbcolumnName());
        dto.setRelationtableIsenable(this.relationtableIsenable);
        dto.setRelationtableModifiedby(this.relationtableModifiedby);
        dto.setRelationtableModifieddt(this.relationtableModifieddt);
        dto.setRelationtableCreateby(this.relationtableCreateby);
        dto.setRelationtableCreatedt(this.relationtableCreatedt);
        dto.setHbcolumnType(this.hbcolumnType);
        return dto;
    }
}