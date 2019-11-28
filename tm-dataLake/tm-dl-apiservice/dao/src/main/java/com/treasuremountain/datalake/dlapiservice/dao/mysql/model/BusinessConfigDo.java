package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import com.treasuremountain.datalake.dlapiservice.common.data.business.BusinessConfigDto;

import java.util.Date;

public class BusinessConfigDo implements BaseDo<BusinessConfigDto> {
    private String businessId;

    private String businessName;

    private String businessDesc;

    private String exchangeId;

    private String exchangeName;

    private Boolean businessIsenable;

    private String businessModifiedby;

    private Date businessModifieddt;

    private String businessCreateby;

    private Date businessCreatedt;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId == null ? null : businessId.trim();
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName == null ? null : businessName.trim();
    }

    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc == null ? null : businessDesc.trim();
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId == null ? null : exchangeId.trim();
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName == null ? null : exchangeName.trim();
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public Boolean getBusinessIsenable() {
        return businessIsenable;
    }

    public void setBusinessIsenable(Boolean businessIsenable) {
        this.businessIsenable = businessIsenable;
    }

    public String getBusinessModifiedby() {
        return businessModifiedby;
    }

    public void setBusinessModifiedby(String businessModifiedby) {
        this.businessModifiedby = businessModifiedby == null ? null : businessModifiedby.trim();
    }

    public Date getBusinessModifieddt() {
        return businessModifieddt;
    }

    public void setBusinessModifieddt(Date businessModifieddt) {
        this.businessModifieddt = businessModifieddt;
    }

    public String getBusinessCreateby() {
        return businessCreateby;
    }

    public void setBusinessCreateby(String businessCreateby) {
        this.businessCreateby = businessCreateby == null ? null : businessCreateby.trim();
    }

    public Date getBusinessCreatedt() {
        return businessCreatedt;
    }

    public void setBusinessCreatedt(Date businessCreatedt) {
        this.businessCreatedt = businessCreatedt;
    }

    @Override
    public BusinessConfigDto toData() {
        BusinessConfigDto dto = new BusinessConfigDto();
        dto.setBusinessId(this.businessId);
        dto.setBusinessName(this.businessName);
        dto.setBusinessDesc(this.businessDesc);
        dto.setExchangeId(this.exchangeId);
        dto.setBusinessIsenable(this.businessIsenable);
        dto.setBusinessModifiedby(this.businessModifiedby);
        dto.setBusinessModifieddt(this.businessModifieddt);
        dto.setBusinessCreateby(this.businessCreateby);
        dto.setBusinessCreatedt(this.businessCreatedt);
        return dto;
    }
}