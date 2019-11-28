package com.treasuremountain.datalake.dlapiservice.common.message;

import java.util.List;

/**
 * Created by gerryzhao on 10/21/2018.
 */
public class RowKv {
    private List<ColumnKv> columnKvList;

    public List<ColumnKv> getColumnKvList() {
        return columnKvList;
    }

    public void setColumnKvList(List<ColumnKv> columnKvList) {
        this.columnKvList = columnKvList;
    }
}
