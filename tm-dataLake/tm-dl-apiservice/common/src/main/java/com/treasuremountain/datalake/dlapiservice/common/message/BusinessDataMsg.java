package com.treasuremountain.datalake.dlapiservice.common.message;

import java.util.List;

/**
 * Created by gerryzhao on 10/21/2018.
 */
public class BusinessDataMsg {
    private String businessId;
    private List<RowKv> rowKvList;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public List<RowKv> getRowKvList() {
        return rowKvList;
    }

    public void setRowKvList(List<RowKv> rowKvList) {
        this.rowKvList = rowKvList;
    }
}
