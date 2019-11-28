package com.treasuremountain.datalake.dlpersistenceservice.application.entity;

import com.treasuremountain.datalake.dlapiservice.common.message.BusinessDataMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.message.QueryCallbackMsg;

import java.util.List;

public class MergeSendData {
    private BusinessDataMsg businessDataMsg;
    private List<QueryCallbackMsg> queryCallbackMsgs;
    private long time;

    public BusinessDataMsg getBusinessDataMsg() {
        return businessDataMsg;
    }

    public void setBusinessDataMsg(BusinessDataMsg businessDataMsg) {
        this.businessDataMsg = businessDataMsg;
    }

    public List<QueryCallbackMsg> getQueryCallbackMsgs() {
        return queryCallbackMsgs;
    }

    public void setQueryCallbackMsgs(List<QueryCallbackMsg> queryCallbackMsgs) {
        this.queryCallbackMsgs = queryCallbackMsgs;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
