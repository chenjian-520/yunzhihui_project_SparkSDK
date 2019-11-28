package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import com.treasuremountain.datalake.dlapiservice.common.data.query.ExchangeConfigDto;

import java.util.Date;

public class ExchangeConfigDo implements BaseDo<ExchangeConfigDto> {
    private String exchangeId;

    private String exchangeName;

    private Boolean exchangeIsenable;

    private String exchangeModifiedby;

    private Date exchangeModifieddt;

    private String exchangeCreateby;

    private Date exchangeCreatedt;

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId == null ? null : exchangeId.trim();
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName == null ? null : exchangeName.trim();
    }

    public Boolean getExchangeIsenable() {
        return exchangeIsenable;
    }

    public void setExchangeIsenable(Boolean exchangeIsenable) {
        this.exchangeIsenable = exchangeIsenable;
    }

    public String getExchangeModifiedby() {
        return exchangeModifiedby;
    }

    public void setExchangeModifiedby(String exchangeModifiedby) {
        this.exchangeModifiedby = exchangeModifiedby == null ? null : exchangeModifiedby.trim();
    }

    public Date getExchangeModifieddt() {
        return exchangeModifieddt;
    }

    public void setExchangeModifieddt(Date exchangeModifieddt) {
        this.exchangeModifieddt = exchangeModifieddt;
    }

    public String getExchangeCreateby() {
        return exchangeCreateby;
    }

    public void setExchangeCreateby(String exchangeCreateby) {
        this.exchangeCreateby = exchangeCreateby == null ? null : exchangeCreateby.trim();
    }

    public Date getExchangeCreatedt() {
        return exchangeCreatedt;
    }

    public void setExchangeCreatedt(Date exchangeCreatedt) {
        this.exchangeCreatedt = exchangeCreatedt;
    }

    @Override
    public ExchangeConfigDto toData() {
        ExchangeConfigDto dto = new ExchangeConfigDto();
        dto.setExchangeId(this.exchangeId);
        dto.setExchangeName(this.exchangeName);
        dto.setExchangeModifiedby(this.exchangeModifiedby);
        dto.setExchangeModifieddt(this.exchangeModifieddt);
        dto.setExchangeCreateby(this.exchangeCreateby);
        dto.setExchangeCreatedt(this.exchangeCreatedt);
        return dto;
    }
}