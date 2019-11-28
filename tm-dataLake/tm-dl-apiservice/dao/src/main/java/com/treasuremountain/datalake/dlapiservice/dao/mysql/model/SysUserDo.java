package com.treasuremountain.datalake.dlapiservice.dao.mysql.model;

import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public class SysUserDo extends SysUserDoKey {

    private String userPassword;

    private String userName;

    private String userAdditionalInfo;

    private String userAuthority;

    private String userEmail;

    private Boolean isActive;

    private Date editDate;

    private String description;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword == null ? null : userPassword.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserAdditionalInfo() {
        return userAdditionalInfo;
    }

    public void setUserAdditionalInfo(String userAdditionalInfo) {
        this.userAdditionalInfo = userAdditionalInfo == null ? null : userAdditionalInfo.trim();
    }

    public String getUserAuthority() {
        return userAuthority;
    }

    public void setUserAuthority(String userAuthority) {
        this.userAuthority = userAuthority == null ? null : userAuthority.trim();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail == null ? null : userEmail.trim();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
    public SysUserDo(String id){
        this.setId(id);
    }

    public SysUserDo(SysUserDo user){
        this.setId(user.getId());
        this.setDescription(user.description);
        this.setIsActive(user.getIsActive());
        this.setUserName(user.getUserName());
        this.setUserAccount(user.getUserAccount());
    }
}