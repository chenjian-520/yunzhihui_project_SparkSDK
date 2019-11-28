package com.tm.dl.javasdk.dpspark.common.entity;

/**
 * @Author:ref.tian
 * @Description:db连接信息
 * @timestamp:2019/11/14 15:23
 **/
public class DBConnectionInfo {

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String url;
    private String username;
    private String password;

}
