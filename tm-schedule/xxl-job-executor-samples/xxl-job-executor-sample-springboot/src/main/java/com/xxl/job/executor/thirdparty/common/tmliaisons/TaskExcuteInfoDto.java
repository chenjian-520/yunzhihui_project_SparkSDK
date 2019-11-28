package com.xxl.job.executor.thirdparty.common.tmliaisons;

public class TaskExcuteInfoDto {
    private String id;
    private String name;
    private String params;
    private String queue;
    private Integer maxExecutors;
    private Integer minExecutors;
    private Integer executoridleTimeout;
    private Integer schedulerbacklogTimeout;
    private String folderId;
    private String businessid;
    private String path;
    private String kettleType;
    private String logLevel;
    private String clusterPosition;
    private String excuteModel;

    //2019/08/13
    private String driverMemory;
    private String executorMemory;
    private String dependInfo;
//    private String dependUser;
//    private String dependItem;
//
//    public String getDependUser() {
//        return dependUser;
//    }
//
//    public void setDependUser(String dependUser) {
//        this.dependUser = dependUser;
//    }
//
//    public String getDependItem() {
//        return dependItem;
//    }
//
//    public void setDependItem(String dependItem) {
//        this.dependItem = dependItem;
//    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public String getDependInfo() {
        return dependInfo;
    }

    public void setDependInfo(String dependInfo) {
        this.dependInfo = dependInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public Integer getMaxExecutors() {
        return maxExecutors;
    }

    public void setMaxExecutors(Integer maxExecutors) {
        this.maxExecutors = maxExecutors;
    }

    public Integer getMinExecutors() {
        return minExecutors;
    }

    public void setMinExecutors(Integer minExecutors) {
        this.minExecutors = minExecutors;
    }

    public Integer getExecutoridleTimeout() {
        return executoridleTimeout;
    }

    public void setExecutoridleTimeout(Integer executoridleTimeout) {
        this.executoridleTimeout = executoridleTimeout;
    }

    public Integer getSchedulerbacklogTimeout() {
        return schedulerbacklogTimeout;
    }

    public void setSchedulerbacklogTimeout(Integer schedulerbacklogTimeout) {
        this.schedulerbacklogTimeout = schedulerbacklogTimeout;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getBusinessid() {
        return businessid;
    }

    public void setBusinessid(String businessid) {
        this.businessid = businessid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKettleType() {
        return kettleType;
    }

    public void setKettleType(String kettleType) {
        this.kettleType = kettleType;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getClusterPosition() {
        return clusterPosition;
    }

    public void setClusterPosition(String clusterPosition) {
        this.clusterPosition = clusterPosition;
    }

    public String getExcuteModel() {
        return excuteModel;
    }

    public void setExcuteModel(String excuteModel) {
        this.excuteModel = excuteModel;
    }
}
