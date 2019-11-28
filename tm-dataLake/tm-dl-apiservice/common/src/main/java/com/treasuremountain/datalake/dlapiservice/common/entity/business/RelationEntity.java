package com.treasuremountain.datalake.dlapiservice.common.entity.business;

/**
 * Created by gerryzhao on 10/21/2018.
 */
public class RelationEntity {
    private String relationtableId;

    private HBTableEntity hbtable;

    private String msgkey;

    private String hbcolumnfamilyName;

    private String hbcolumnName;

    public String getHbcolumnType() {
        return hbcolumnType;
    }

    public void setHbcolumnType(String hbcolumnType) {
        this.hbcolumnType = hbcolumnType;
    }

    private String hbcolumnType;

    public String getRelationtableId() {
        return relationtableId;
    }

    public void setRelationtableId(String relationtableId) {
        this.relationtableId = relationtableId;
    }

    public HBTableEntity getHbtable() {
        return hbtable;
    }

    public void setHbtable(HBTableEntity hbtable) {
        this.hbtable = hbtable;
    }

    public String getMsgkey() {
        return msgkey;
    }

    public void setMsgkey(String msgkey) {
        this.msgkey = msgkey;
    }

    public String getHbcolumnfamilyName() {
        return hbcolumnfamilyName;
    }

    public void setHbcolumnfamilyName(String hbcolumnfamilyName) {
        this.hbcolumnfamilyName = hbcolumnfamilyName;
    }

    public String getHbcolumnName() {
        return hbcolumnName;
    }

    public void setHbcolumnName(String hbcolumnName) {
        this.hbcolumnName = hbcolumnName;
    }
}
