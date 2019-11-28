package com.treasuremountain.datalake.dlapiservice.service.kafka.producer;

public interface ICallback {
    public void msgSendCallback(Boolean isSuccess);
}
