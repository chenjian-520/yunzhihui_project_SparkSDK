package com.treasuremountain.datalake.dlapiservice.common.entity.business;

/**
 * @version 1.0
 * @program: dlapiservice->TopicEntity
 * @description: Topic实体类
 * @author: Axin
 * @create: 2019-11-15 22:40
 **/
public class TopicEntity {
    private String topicId;
    private String topicName;
    private String groupId;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
