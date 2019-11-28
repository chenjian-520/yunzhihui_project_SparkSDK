package com.xxl.job.admin.core.model;

import java.util.Date;

public class ThirdpartyClassDo {
    private String thirdpartyclassConfigId;

    private String thirdpartyclassName;

    private String thirdpartyclassClasspah;

    private Boolean thirdpartyclassIsenabled;

    private String thirdpartyclassCreateby;

    private Date thirdpartyclassCreatedt;

    private String thirdpartyclassModifiedby;

    private Date thirdpartyclassModifieddt;

    public String getThirdpartyclassConfigId() {
        return thirdpartyclassConfigId;
    }

    public void setThirdpartyclassConfigId(String thirdpartyclassConfigId) {
        this.thirdpartyclassConfigId = thirdpartyclassConfigId == null ? null : thirdpartyclassConfigId.trim();
    }

    public String getThirdpartyclassName() {
        return thirdpartyclassName;
    }

    public void setThirdpartyclassName(String thirdpartyclassName) {
        this.thirdpartyclassName = thirdpartyclassName == null ? null : thirdpartyclassName.trim();
    }

    public String getThirdpartyclassClasspah() {
        return thirdpartyclassClasspah;
    }

    public void setThirdpartyclassClasspah(String thirdpartyclassClasspah) {
        this.thirdpartyclassClasspah = thirdpartyclassClasspah == null ? null : thirdpartyclassClasspah.trim();
    }

    public Boolean getThirdpartyclassIsenabled() {
        return thirdpartyclassIsenabled;
    }

    public void setThirdpartyclassIsenabled(Boolean thirdpartyclassIsenabled) {
        this.thirdpartyclassIsenabled = thirdpartyclassIsenabled;
    }

    public String getThirdpartyclassCreateby() {
        return thirdpartyclassCreateby;
    }

    public void setThirdpartyclassCreateby(String thirdpartyclassCreateby) {
        this.thirdpartyclassCreateby = thirdpartyclassCreateby == null ? null : thirdpartyclassCreateby.trim();
    }

    public Date getThirdpartyclassCreatedt() {
        return thirdpartyclassCreatedt;
    }

    public void setThirdpartyclassCreatedt(Date thirdpartyclassCreatedt) {
        this.thirdpartyclassCreatedt = thirdpartyclassCreatedt;
    }

    public String getThirdpartyclassModifiedby() {
        return thirdpartyclassModifiedby;
    }

    public void setThirdpartyclassModifiedby(String thirdpartyclassModifiedby) {
        this.thirdpartyclassModifiedby = thirdpartyclassModifiedby == null ? null : thirdpartyclassModifiedby.trim();
    }

    public Date getThirdpartyclassModifieddt() {
        return thirdpartyclassModifieddt;
    }

    public void setThirdpartyclassModifieddt(Date thirdpartyclassModifieddt) {
        this.thirdpartyclassModifieddt = thirdpartyclassModifieddt;
    }
}