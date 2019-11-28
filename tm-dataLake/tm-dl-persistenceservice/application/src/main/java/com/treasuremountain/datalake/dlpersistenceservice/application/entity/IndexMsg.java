package com.treasuremountain.datalake.dlpersistenceservice.application.entity;

import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSourceBatchMsg;

import java.util.List;

public class IndexMsg {
    private String indexName;
    private List<IndexBody> indexBodyList;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public List<IndexBody> getIndexBodyList() {
        return indexBodyList;
    }

    public void setIndexBodyList(List<IndexBody> indexBodyList) {
        this.indexBodyList = indexBodyList;
    }
}
