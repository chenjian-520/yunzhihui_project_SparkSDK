package com.treasuremountain.tmcommon.thirdpartyservice.httpclient.message;

/**
 * Created by gerryzhao on 6/9/2018.
 */
public class MTHttpCallbackParms {
    private String responseBody;

    private int statusCode;

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
