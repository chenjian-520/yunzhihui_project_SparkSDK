package com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message;

import java.util.List;

public class HistEntity {
    private List<HistObjEntity> hits;

    public List<HistObjEntity> getHits() {
        return hits;
    }

    public void setHits(List<HistObjEntity> hits) {
        this.hits = hits;
    }
}
