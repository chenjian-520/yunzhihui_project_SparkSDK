package com.treasuremountain.datalake.dlapiservice.common.data.kafka;

/**
 * @version 1.0
 * @program: dlapiservice->KafkaMsgDto
 * @description: KafkaMsgDto
 * @author: Axin
 * @create: 2019-11-15 23:07
 **/
public class KafkaMsgDto {
    private String topic;
    private String msg;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
