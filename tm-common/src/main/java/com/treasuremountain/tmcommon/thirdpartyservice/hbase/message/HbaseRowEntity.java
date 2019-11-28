package com.treasuremountain.tmcommon.thirdpartyservice.hbase.message;

import java.util.List;

/**
 * Created by gerryzhao on 10/16/2018.
 */
public class HbaseRowEntity {

    private String rowKey;
    private String salt;
    private List<HbaseColumnFamilyEntity> columnFamily;

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<HbaseColumnFamilyEntity> getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(List<HbaseColumnFamilyEntity> columnFamily) {
        this.columnFamily = columnFamily;
    }
}
