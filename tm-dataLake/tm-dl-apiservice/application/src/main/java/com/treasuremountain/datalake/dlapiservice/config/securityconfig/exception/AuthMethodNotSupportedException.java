package com.treasuremountain.datalake.dlapiservice.config.securityconfig.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/5.
 * Company: Maxnerva
 * Project: polaris
 */
public class AuthMethodNotSupportedException extends AuthenticationServiceException {

    private static final long serialVersionUID = 3705043083010304496L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }
}
