package com.treasuremountain.datalake.dlapiservice.common.message;

import com.treasuremountain.datalake.dlapiservice.common.entity.business.SimpleDataInfo;

import java.util.List;

public class HbaseSimpleMsg {
    private String isNextPage;
    private SimpleDataInfo fmtInfo;
    private List<String> data;

    public String getIsNextPage() {
        return isNextPage;
    }

    public void setIsNextPage(String isNextPage) {
        this.isNextPage = isNextPage;
    }

    public SimpleDataInfo getFmtInfo() {
        return fmtInfo;
    }

    public void setFmtInfo(SimpleDataInfo fmtInfo) {
        this.fmtInfo = fmtInfo;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
