package com.treasuremountain.machinecloudreceiver.dao.model;

import com.treasuremountain.machinecloudreceiver.common.data.DatarelationConfigDto;

import java.util.Date;

public class DatarelationConfigDo  implements BaseDo<DatarelationConfigDto> {
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
        this.datarelationId = datarelationId == null ? null : datarelationId.trim();
    }

    public String getDatarelationKey() {
        return datarelationKey;
    }

    public void setDatarelationKey(String datarelationKey) {
        this.datarelationKey = datarelationKey == null ? null : datarelationKey.trim();
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId == null ? null : businessId.trim();
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
        this.datarelationModifiedby = datarelationModifiedby == null ? null : datarelationModifiedby.trim();
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
        this.datarelationCreateby = datarelationCreateby == null ? null : datarelationCreateby.trim();
    }

    public Date getDatarelationCreatedt() {
        return datarelationCreatedt;
    }

    public void setDatarelationCreatedt(Date datarelationCreatedt) {
        this.datarelationCreatedt = datarelationCreatedt;
    }

    @Override
    public DatarelationConfigDto toData() {
        DatarelationConfigDto dto = new DatarelationConfigDto();
        dto.setDatarelationId(this.datarelationId);
        dto.setDatarelationKey(this.datarelationKey);
        dto.setBusinessId(this.businessId);
        dto.setDatarelationIsenable(this.datarelationIsenable);
        dto.setDatarelationModifiedby(this.datarelationModifiedby);
        dto.setDatarelationModifieddt(this.datarelationModifieddt);
        dto.setDatarelationCreateby(this.datarelationCreateby);
        dto.setDatarelationCreatedt(this.datarelationCreatedt);
        return dto;
    }
}