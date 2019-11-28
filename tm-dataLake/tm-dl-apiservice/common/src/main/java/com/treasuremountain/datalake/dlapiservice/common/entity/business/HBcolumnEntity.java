package com.treasuremountain.datalake.dlapiservice.common.entity.business;

/**
 * Created by gerryzhao on 10/21/2018.
 */
public class HBcolumnEntity {
    private String hbcolumnId;

    private String hbcolumnfamilyId;

    private String hbcolumnName;

    private Boolean hbcolumnIsindex;

    public String getHbcolumnType() {
        return hbcolumnType;
    }

    public void setHbcolumnType(String hbcolumnType) {
        this.hbcolumnType = hbcolumnType;
    }

    private String  hbcolumnType;

    public String getHbcolumnId() {
        return hbcolumnId;
    }

    public void setHbcolumnId(String hbcolumnId) {
        this.hbcolumnId = hbcolumnId;
    }

    public String getHbcolumnfamilyId() {
        return hbcolumnfamilyId;
    }

    public void setHbcolumnfamilyId(String hbcolumnfamilyId) {
        this.hbcolumnfamilyId = hbcolumnfamilyId;
    }

    public String getHbcolumnName() {
        return hbcolumnName;
    }

    public void setHbcolumnName(String hbcolumnName) {
        this.hbcolumnName = hbcolumnName;
    }

    public Boolean getHbcolumnIsindex() {
        return hbcolumnIsindex;
    }

    public void setHbcolumnIsindex(Boolean hbcolumnIsindex) {
        this.hbcolumnIsindex = hbcolumnIsindex;
    }
}
