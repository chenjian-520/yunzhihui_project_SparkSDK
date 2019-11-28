package com.treasuremountain.datalake.dlpersistenceservice.application.entity;

import com.treasuremountain.datalake.dlapiservice.common.message.BusinessDataMsg;
import com.treasuremountain.datalake.dlapiservice.common.message.RowKv;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.message.QueryCallbackMsg;

import java.util.List;

public class MergeData {

    private BusinessDataMsg businessDataMsg;
    private QueryCallbackMsg callbackMsg;

    public BusinessDataMsg getBusinessDataMsg() {
        return businessDataMsg;
    }

    public void setBusinessDataMsg(BusinessDataMsg businessDataMsg) {
        this.businessDataMsg = businessDataMsg;
    }

    public QueryCallbackMsg getCallbackMsg() {
        return callbackMsg;
    }

    public void setCallbackMsg(QueryCallbackMsg callbackMsg) {
        this.callbackMsg = callbackMsg;
    }
}
