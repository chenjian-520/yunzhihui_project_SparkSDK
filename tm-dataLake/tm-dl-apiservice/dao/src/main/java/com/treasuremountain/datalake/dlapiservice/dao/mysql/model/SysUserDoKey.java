package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

public class SysUserDoKey {
    private String id;

    private String userAccount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount == null ? null : userAccount.trim();
    }
}