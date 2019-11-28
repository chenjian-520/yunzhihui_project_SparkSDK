package com.treasuremountain.tmcommon.thirdpartyservice.hbase.message;

public class HbaseFilterParmMsg {
    private String columnFamily;
    private String column;
    private String value;
    private HbaseFilterParmType type;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public HbaseFilterParmType getType() {
        return type;
    }

    public void setType(HbaseFilterParmType type) {
        this.type = type;
    }
}
