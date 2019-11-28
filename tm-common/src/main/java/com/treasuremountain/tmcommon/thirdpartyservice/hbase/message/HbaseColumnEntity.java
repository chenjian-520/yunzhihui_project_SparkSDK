package com.treasuremountain.tmcommon.thirdpartyservice.hbase.message;

/**
 * Created by gerryzhao on 10/16/2018.
 */
public class HbaseColumnEntity {
    private String columnName;
    private String columnValue;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }
}
