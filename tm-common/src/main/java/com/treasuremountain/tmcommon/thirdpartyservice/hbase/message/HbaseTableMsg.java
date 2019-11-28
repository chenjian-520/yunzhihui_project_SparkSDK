package com.treasuremountain.tmcommon.thirdpartyservice.hbase.message;

import java.util.List;

/**
 * Created by gerryzhao on 10/16/2018.
 */
public class HbaseTableMsg {

    private String tableName;
    private long showCount;
    private String isNextPage;
    private List<HbaseRowEntity> rowList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public long getShowCount() {
        return showCount;
    }

    public void setShowCount(long showCount) {
        this.showCount = showCount;
    }

    public String getIsNextPage() {
        return isNextPage;
    }

    public void setIsNextPage(String isNextPage) {
        this.isNextPage = isNextPage;
    }

    public List<HbaseRowEntity> getRowList() {
        return rowList;
    }

    public void setRowList(List<HbaseRowEntity> rowList) {
        this.rowList = rowList;
    }
}
