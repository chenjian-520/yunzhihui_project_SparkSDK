package com.treasuremountain.datalake.dlapiservice.config.securityconfig.exception;

import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token.JwtToken;
import org.springframework.security.core.AuthenticationException;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/5.
 * Company: Maxnerva
 * Project: polaris
 */
public class JwtExpiredTokenException extends AuthenticationException {

    private static final long serialVersionUID = -5959543783324224864L;

    private JwtToken token;

    public JwtExpiredTokenException(String msg) {
        super(msg);
    }

    public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}
