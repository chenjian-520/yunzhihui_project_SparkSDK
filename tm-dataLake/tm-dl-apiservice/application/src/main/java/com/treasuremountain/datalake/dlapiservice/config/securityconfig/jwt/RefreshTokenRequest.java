package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/5.
 * Company: Maxnerva
 * Project: polaris
 */
public class RefreshTokenRequest {

    private String refreshToken;

    @JsonCreator
    public RefreshTokenRequest(@JsonProperty("refreshToken") String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
