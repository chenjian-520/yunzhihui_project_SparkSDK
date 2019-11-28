package com.treasuremountain.datalake.dlpersistenceservice.application.entity;

import java.util.List;

public class IndexBody {
    private String id;
    private List<IndexValue> body;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<IndexValue> getBody() {
        return body;
    }

    public void setBody(List<IndexValue> body) {
        this.body = body;
    }
}
