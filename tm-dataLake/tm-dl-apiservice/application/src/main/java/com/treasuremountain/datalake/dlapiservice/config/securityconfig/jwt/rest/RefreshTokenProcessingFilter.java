/**
* Copyright © 2016-2017 The Polarisboard Authors
 */
package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.exception.AuthMethodNotSupportedException;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.RefreshTokenRequest;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token.RawAccessJwtToken;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token.RefreshAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RefreshTokenProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static Logger logger = LoggerFactory.getLogger(RefreshTokenProcessingFilter.class);

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    private final ObjectMapper objectMapper;

    public RefreshTokenProcessingFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler,
                                        AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            if(logger.isDebugEnabled()) {
                logger.info("Authentication method not supported. Request method: " + request.getMethod());
            }
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        RefreshTokenRequest refreshTokenRequest;
        try {
            refreshTokenRequest = objectMapper.readValue(request.getReader(), RefreshTokenRequest.class);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Invalid refresh token request payload");
        }

        if (StringUtils.isBlank(refreshTokenRequest.getRefreshToken())) {
            throw new AuthenticationServiceException("Refresh token is not provided");
        }

        RawAccessJwtToken token = new RawAccessJwtToken(refreshTokenRequest.getRefreshToken());

        return this.getAuthenticationManager().authenticate(new RefreshAuthenticationToken(token));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
