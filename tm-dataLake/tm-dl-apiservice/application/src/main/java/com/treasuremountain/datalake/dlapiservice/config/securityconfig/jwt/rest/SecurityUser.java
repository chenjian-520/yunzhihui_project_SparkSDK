package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/5.
 * Company: Maxnerva
 * Project: polaris
 */
@Data
public class SecurityUser implements UserDetails {

    private static final long serialVersionUID = -797397440703066079L;

    private String password;
    private String userAccount;
    private boolean isEnable;
    //授权
    private Collection<? extends GrantedAuthority> authorities;
    private String userId;
    //    private String accountRole;
    private Date lastLoginDate;
    private String accountName;

    public SecurityUser(String useraccount, Collection<? extends GrantedAuthority> authorities) {
        this.userAccount = useraccount;
        this.authorities = authorities;
    }

    public SecurityUser(String password, String useraccount, Collection<? extends GrantedAuthority> authorities) {
        this.userAccount = useraccount;
        this.authorities = authorities;
        this.password = password;
    }
    public SecurityUser(String userId) {
        this.setUserId(userId);
    }
    public SecurityUser() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.userAccount;
    }

    public void setAuthority(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnable;
    }

    public void setEnabled(boolean isEnable) {
        this.isEnable = isEnable;
    }
}
