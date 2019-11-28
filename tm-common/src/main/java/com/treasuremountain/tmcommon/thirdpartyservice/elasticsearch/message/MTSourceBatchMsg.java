package com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message;

import net.sf.json.JSONObject;

public class MTSourceBatchMsg {
    private String id;
    private JSONObject body;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }
}
