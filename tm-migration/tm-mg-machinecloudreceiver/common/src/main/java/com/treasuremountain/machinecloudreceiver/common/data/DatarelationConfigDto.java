package com.treasuremountain.machinecloudreceiver.common.data;

import java.util.Date;

public class DatarelationConfigDto {

    private String datarelationId;

    private String datarelationKey;

    private String businessId;

    private Boolean datarelationIsenable;

    private String datarelationModifiedby;

    private Date datarelationModifieddt;

    private String datarelationCreateby;

    private Date datarelationCreatedt;

    public String getDatarelationId() {
        return datarelationId;
    }

    public void setDatarelationId(String datarelationId) {
        this.datarelationId = datarelationId;
    }

    public String getDatarelationKey() {
        return datarelationKey;
    }

    public void setDatarelationKey(String datarelationKey) {
        this.datarelationKey = datarelationKey;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Boolean getDatarelationIsenable() {
        return datarelationIsenable;
    }

    public void setDatarelationIsenable(Boolean datarelationIsenable) {
        this.datarelationIsenable = datarelationIsenable;
    }

    public String getDatarelationModifiedby() {
        return datarelationModifiedby;
    }

    public void setDatarelationModifiedby(String datarelationModifiedby) {
        this.datarelationModifiedby = datarelationModifiedby;
    }

    public Date getDatarelationModifieddt() {
        return datarelationModifieddt;
    }

    public void setDatarelationModifieddt(Date datarelationModifieddt) {
        this.datarelationModifieddt = datarelationModifieddt;
    }

    public String getDatarelationCreateby() {
        return datarelationCreateby;
    }

    public void setDatarelationCreateby(String datarelationCreateby) {
        this.datarelationCreateby = datarelationCreateby;
    }

    public Date getDatarelationCreatedt() {
        return datarelationCreatedt;
    }

    public void setDatarelationCreatedt(Date datarelationCreatedt) {
        this.datarelationCreatedt = datarelationCreatedt;
    }
}
