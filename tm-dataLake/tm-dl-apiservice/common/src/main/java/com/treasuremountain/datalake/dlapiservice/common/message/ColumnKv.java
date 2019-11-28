package com.treasuremountain.datalake.dlapiservice.common.message;


/**
 * Created by gerryzhao on 10/21/2018.
 */
public class ColumnKv {
    private String key;
    private String value;
    private TMDLType type;

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

    public TMDLType getType() {
        return type;
    }

    public void setType(TMDLType type) {
        this.type = type;
    }
}
