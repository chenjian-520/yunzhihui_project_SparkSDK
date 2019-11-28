package com.xxl.job.admin.core.model;

import java.util.Date;

public class ExecutorChoiceDo {
    private String executorchoiceConfigId;

    private String executorchoiceExecutor;

    private String executorchoiceLocation;

    private String executorchoiceType;

    private String executorchoiceUri;

    private Boolean executorchoiceIsenabled;

    private String executorchoiceCreateby;

    private Date executorchoiceCreatedt;

    private String executorchoiceModifiedby;

    private Date executorchoiceModifieddt;

    public String getExecutorchoiceConfigId() {
        return executorchoiceConfigId;
    }

    public void setExecutorchoiceConfigId(String executorchoiceConfigId) {
        this.executorchoiceConfigId = executorchoiceConfigId == null ? null : executorchoiceConfigId.trim();
    }

    public String getExecutorchoiceExecutor() {
        return executorchoiceExecutor;
    }

    public void setExecutorchoiceExecutor(String executorchoiceExecutor) {
        this.executorchoiceExecutor = executorchoiceExecutor == null ? null : executorchoiceExecutor.trim();
    }

    public String getExecutorchoiceLocation() {
        return executorchoiceLocation;
    }

    public void setExecutorchoiceLocation(String executorchoiceLocation) {
        this.executorchoiceLocation = executorchoiceLocation == null ? null : executorchoiceLocation.trim();
    }

    public String getExecutorchoiceType() {
        return executorchoiceType;
    }

    public void setExecutorchoiceType(String executorchoiceType) {
        this.executorchoiceType = executorchoiceType == null ? null : executorchoiceType.trim();
    }

    public String getExecutorchoiceUri() {
        return executorchoiceUri;
    }

    public void setExecutorchoiceUri(String executorchoiceUri) {
        this.executorchoiceUri = executorchoiceUri == null ? null : executorchoiceUri.trim();
    }

    public Boolean getExecutorchoiceIsenabled() {
        return executorchoiceIsenabled;
    }

    public void setExecutorchoiceIsenabled(Boolean executorchoiceIsenabled) {
        this.executorchoiceIsenabled = executorchoiceIsenabled;
    }

    public String getExecutorchoiceCreateby() {
        return executorchoiceCreateby;
    }

    public void setExecutorchoiceCreateby(String executorchoiceCreateby) {
        this.executorchoiceCreateby = executorchoiceCreateby == null ? null : executorchoiceCreateby.trim();
    }

    public Date getExecutorchoiceCreatedt() {
        return executorchoiceCreatedt;
    }

    public void setExecutorchoiceCreatedt(Date executorchoiceCreatedt) {
        this.executorchoiceCreatedt = executorchoiceCreatedt;
    }

    public String getExecutorchoiceModifiedby() {
        return executorchoiceModifiedby;
    }

    public void setExecutorchoiceModifiedby(String executorchoiceModifiedby) {
        this.executorchoiceModifiedby = executorchoiceModifiedby == null ? null : executorchoiceModifiedby.trim();
    }

    public Date getExecutorchoiceModifieddt() {
        return executorchoiceModifieddt;
    }

    public void setExecutorchoiceModifieddt(Date executorchoiceModifieddt) {
        this.executorchoiceModifieddt = executorchoiceModifieddt;
    }
}