package com.xxl.job.executor.thirdparty.executor.spark;

public class SparkParm {
    private String id;
    private String controlcode;
    private String queue;
    private int maxExecutors;
    private int miExecutors;
    private int executoridleTimeout;
    private int schedulerbacklogTimeout;
    private String businessid;
    private String params;

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

    public String getControlcode() {
        return controlcode;
    }

    public void setControlcode(String controlcode) {
        this.controlcode = controlcode;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public int getMaxExecutors() {
        return maxExecutors;
    }

    public void setMaxExecutors(int maxExecutors) {
        this.maxExecutors = maxExecutors;
    }

    public int getMiExecutors() {
        return miExecutors;
    }

    public void setMiExecutors(int miExecutors) {
        this.miExecutors = miExecutors;
    }

    public int getExecutoridleTimeout() {
        return executoridleTimeout;
    }

    public void setExecutoridleTimeout(int executoridleTimeout) {
        this.executoridleTimeout = executoridleTimeout;
    }

    public int getSchedulerbacklogTimeout() {
        return schedulerbacklogTimeout;
    }

    public void setSchedulerbacklogTimeout(int schedulerbacklogTimeout) {
        this.schedulerbacklogTimeout = schedulerbacklogTimeout;
    }

    public String getBusinessid() {
        return businessid;
    }

    public void setBusinessid(String businessid) {
        this.businessid = businessid;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
