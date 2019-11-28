package com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message;

import java.util.List;

/**
 * Created by gerryzhao on 10/29/2018.
 */
public class MTSearchContentMsg {
    private List<List<String>> rows;

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
}
