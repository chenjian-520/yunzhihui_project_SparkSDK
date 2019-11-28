package com.treasuremountain.datalake.dlpersistenceservice.application.entity;

import com.treasuremountain.datalake.dlapiservice.common.message.TMDLType;

public class IndexValue {
    private TMDLType type;
    private String key;
    private String value;

    public TMDLType getType() {
        return type;
    }

    public void setType(TMDLType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
