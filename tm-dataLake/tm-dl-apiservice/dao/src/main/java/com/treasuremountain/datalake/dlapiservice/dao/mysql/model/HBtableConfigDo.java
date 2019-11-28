package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBtableConfigDto;

import java.util.Date;

public class HBtableConfigDo implements BaseDo<HBtableConfigDto> {
    private String hbtableId;

    private String hbtableName;

    private Boolean hbtableIscompression;

    private String hbtableCompressionname;

    private Boolean hbtableIssplit;

    private String hbtableSplitinfo;

    private String hbtableDesc;

    private Boolean hbtableIstablesegment;

    private int hbtablesegmenttime;

    private int hbtableretentiontime;

    private String hbcurrenttablename;

    private Boolean hbtableIstwoLevelIndex;

    private int hbindexsegmenttime;

    private int hbindexretentiontime;

    private String hbcurrentindexname;

    private Boolean hbtableIsenable;

    private Boolean initResult;

    private String hbtableModifiedby;

    private Date hbtableModifieddt;

    private String hbtableCreateby;

    private Date hbtableCreatedt;

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

    public Boolean getHbtableIscompression() {
        return hbtableIscompression;
    }

    public void setHbtableIscompression(Boolean hbtableIscompression) {
        this.hbtableIscompression = hbtableIscompression;
    }

    public String getHbtableCompressionname() {
        return hbtableCompressionname;
    }

    public void setHbtableCompressionname(String hbtableCompressionname) {
        this.hbtableCompressionname = hbtableCompressionname == null ? null : hbtableCompressionname.trim();
    }

    public Boolean getHbtableIssplit() {
        return hbtableIssplit;
    }

    public void setHbtableIssplit(Boolean hbtableIssplit) {
        this.hbtableIssplit = hbtableIssplit;
    }

    public String getHbtableSplitinfo() {
        return hbtableSplitinfo;
    }

    public void setHbtableSplitinfo(String hbtableSplitinfo) {
        this.hbtableSplitinfo = hbtableSplitinfo == null ? null : hbtableSplitinfo.trim();
    }

    public String getHbtableDesc() {
        return hbtableDesc;
    }

    public void setHbtableDesc(String hbtableDesc) {
        this.hbtableDesc = hbtableDesc == null ? null : hbtableDesc.trim();
    }

    public Boolean getHbtableIstablesegment() {
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

    public Boolean getHbtableIsenable() {
        return hbtableIsenable;
    }

    public void setHbtableIsenable(Boolean hbtableIsenable) {
        this.hbtableIsenable = hbtableIsenable;
    }

    public Boolean getInitResult() {
        return initResult;
    }

    public void setInitResult(Boolean initResult) {
        this.initResult = initResult;
    }

    public Boolean getHbtableIstwoLevelIndex() {
        return hbtableIstwoLevelIndex;
    }

    public void setHbtableIstwoLevelIndex(Boolean hbtableIstwoLevelIndex) {
        this.hbtableIstwoLevelIndex = hbtableIstwoLevelIndex;
    }

    public int getHbindexsegmenttime() {
        return hbindexsegmenttime;
    }

    public void setHbindexsegmenttime(int hbsegmenttime) {
        this.hbindexsegmenttime = hbsegmenttime;
    }

    public int getHbindexretentiontime() {
        return hbindexretentiontime;
    }

    public void setHbindexretentiontime(int hbretentiontime) {
        this.hbindexretentiontime = hbretentiontime;
    }

    public String getHbcurrentindexname() {
        return hbcurrentindexname;
    }

    public void setHbcurrentindexname(String hbcurrentindexname) {
        this.hbcurrentindexname = hbcurrentindexname;
    }

    public String getHbtableModifiedby() {
        return hbtableModifiedby;
    }

    public void setHbtableModifiedby(String hbtableModifiedby) {
        this.hbtableModifiedby = hbtableModifiedby == null ? null : hbtableModifiedby.trim();
    }

    public Date getHbtableModifieddt() {
        return hbtableModifieddt;
    }

    public void setHbtableModifieddt(Date hbtableModifieddt) {
        this.hbtableModifieddt = hbtableModifieddt;
    }

    public String getHbtableCreateby() {
        return hbtableCreateby;
    }

    public void setHbtableCreateby(String hbtableCreateby) {
        this.hbtableCreateby = hbtableCreateby == null ? null : hbtableCreateby.trim();
    }

    public Date getHbtableCreatedt() {
        return hbtableCreatedt;
    }

    public void setHbtableCreatedt(Date hbtableCreatedt) {
        this.hbtableCreatedt = hbtableCreatedt;
    }

    @Override
    public HBtableConfigDto toData() {
        HBtableConfigDto dto = new HBtableConfigDto();
        dto.setHbtableId(this.hbtableId);
        dto.setHbtableName(this.hbtableName);
        dto.setHbtableIscompression(this.hbtableIscompression);
        dto.setHbtableCompressionname(this.hbtableCompressionname);
        dto.setHbtableIssplit(this.hbtableIssplit);
        dto.setHbtableSplitinfo(this.hbtableSplitinfo);
        dto.setHbtableDesc(this.hbtableDesc);
        dto.setHbtableIstablesegment(hbtableIstablesegment);
        dto.setHbtablesegmenttime(hbtablesegmenttime);
        dto.setHbtableretentiontime(hbtableretentiontime);
        dto.setHbcurrenttablename(hbcurrenttablename);
        dto.setHbtableIstwoLevelIndex(this.hbtableIstwoLevelIndex);
        dto.setHbindexsegmenttime(this.hbindexsegmenttime);
        dto.setHbindexretentiontime(this.hbindexretentiontime);
        dto.setHbcurrentindexname(this.hbcurrentindexname);
        dto.setHbtableIsenable(this.hbtableIsenable);
        dto.setInitResult(this.initResult);
        dto.setHbtableModifiedby(this.hbtableModifiedby);
        dto.setHbtableModifieddt(this.hbtableModifieddt);
        dto.setHbtableCreateby(this.hbtableCreateby);
        dto.setHbtableCreatedt(this.hbtableCreatedt);
        return dto;
    }
}