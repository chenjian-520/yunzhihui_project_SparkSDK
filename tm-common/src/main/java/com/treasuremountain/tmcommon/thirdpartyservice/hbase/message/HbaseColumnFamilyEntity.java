package com.treasuremountain.tmcommon.thirdpartyservice.hbase.message;

import java.util.List;

/**
 * Created by gerryzhao on 10/16/2018.
 */
public class HbaseColumnFamilyEntity {

    private String columnFamilyName;
    private List<HbaseColumnEntity> columnList;

    public String getColumnFamilyName() {
        return columnFamilyName;
    }

    public void setColumnFamilyName(String columnFamilyName) {
        this.columnFamilyName = columnFamilyName;
    }

    public List<HbaseColumnEntity> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<HbaseColumnEntity> columnList) {
        this.columnList = columnList;
    }
}
