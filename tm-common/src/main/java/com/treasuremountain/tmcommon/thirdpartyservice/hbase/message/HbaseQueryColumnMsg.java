package com.treasuremountain.tmcommon.thirdpartyservice.hbase.message;

public class HbaseQueryColumnMsg {
    private String columnFamily;
    private String column;

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
