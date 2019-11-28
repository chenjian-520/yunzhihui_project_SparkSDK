package com.treasuremountain.machinecollector.config.securityconfig;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Created by gerrywjzhao on 7/6/2017.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private JwtUser user;

    public String getToken() {
        return token;
    }

    private String token;

    public JwtAuthenticationToken(JwtUser user)
    {
        super(user.getAuthorities());
        this.eraseCredentials();
        this.user = user;
        super.setAuthenticated(true);
    }

    public JwtAuthenticationToken(String token)
    {
        super(null);
        this.token = token;
        this.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.token = null;
    }
}
