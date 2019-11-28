package com.xxl.job.executor.thirdparty.common.tmliaisons;

public class ScheduleInfoDto {
    private String id;
    private String xxljobId;
    private String taskType;
    private String scheduleParams;
    private TaskExcuteInfoDto taskExcuteInfoDto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getXxljobId() {
        return xxljobId;
    }

    public void setXxljobId(String xxljobId) {
        this.xxljobId = xxljobId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getScheduleParams() {
        return scheduleParams;
    }

    public void setScheduleParams(String scheduleParams) {
        this.scheduleParams = scheduleParams;
    }

    public TaskExcuteInfoDto getTaskExcuteInfoDto() {
        return taskExcuteInfoDto;
    }

    public void setTaskExcuteInfoDto(TaskExcuteInfoDto taskExcuteInfoDto) {
        this.taskExcuteInfoDto = taskExcuteInfoDto;
    }
}
