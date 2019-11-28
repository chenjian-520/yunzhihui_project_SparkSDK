package com.tm.dl.javasdk.dpspark.common.entity;

import com.alibaba.fastjson.JSONObject;
import scala.Serializable;

/**
 * Description:  com.tm.dl.javasdk.dpspark.common.entity
 * Copyright: © 2019 Maxnerva. All rights reserved.
 * Company: IISD
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/8
 */
public class DPKafkaInfo implements Serializable {
    private String serverUrl;
    private String topics;
    private String groupId;
    private Long batchDuration;
    private Long windowDurationMultiple;
    private Long sliverDurationMultiple;
    private JSONObject kafkaConf;

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getBatchDuration() {
        return batchDuration;
    }

    public void setBatchDuration(Long batchDuration) {
        this.batchDuration = batchDuration;
    }

    public Long getWindowDurationMultiple() {
        return windowDurationMultiple;
    }

    public void setWindowDurationMultiple(Long windowDurationMultiple) {
        this.windowDurationMultiple = windowDurationMultiple;
    }

    public Long getSliverDurationMultiple() {
        return sliverDurationMultiple;
    }

    public void setSliverDurationMultiple(Long sliverDurationMultiple) {
        this.sliverDurationMultiple = sliverDurationMultiple;
    }

    public JSONObject getKafkaConf() {
        return kafkaConf;
    }

    public void setKafkaConf(JSONObject kafkaConf) {
        this.kafkaConf = kafkaConf;
    }
}
