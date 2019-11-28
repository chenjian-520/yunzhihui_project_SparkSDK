package com.treasuremountain.datalake.dlapiservice.common.entity.business;

import java.util.List;

/**
 * Created by gerryzhao on 10/21/2018.
 */
public class HBcolumnfamilyEntity {
    private String hbcolumnfamilyId;

    private String hbcolumnfamilyName;

    private List<HBcolumnEntity>  columnList;

    public String getHbcolumnfamilyId() {
        return hbcolumnfamilyId;
    }

    public void setHbcolumnfamilyId(String hbcolumnfamilyId) {
        this.hbcolumnfamilyId = hbcolumnfamilyId;
    }

    public String getHbcolumnfamilyName() {
        return hbcolumnfamilyName;
    }

    public void setHbcolumnfamilyName(String hbcolumnfamilyName) {
        this.hbcolumnfamilyName = hbcolumnfamilyName;
    }

    public List<HBcolumnEntity> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<HBcolumnEntity> columnList) {
        this.columnList = columnList;
    }
}
