package com.treasuremountain.datalake.dlapiservice.config.securityconfig.exception;


import com.fasterxml.jackson.annotation.JsonValue;

/**
 *edge server 错误代码
 * ref.tian
 * */
public enum EdgeServerErrorCode {

    GENERAL(2),
    AUTHENTICATION(10),
    JWT_TOKEN_EXPIRED(11),
    JWT_TOKEN_INVALID(12),
    PERMISSION_DENIED(20),
    INVALID_ARGUMENTS(30),
    BAD_REQUEST_PARAMS(31),
    ITEM_NOT_FOUND(32);

    private int errorCode;

    EdgeServerErrorCode(int errorCode){
        this.errorCode = errorCode;
    }

    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }
}


