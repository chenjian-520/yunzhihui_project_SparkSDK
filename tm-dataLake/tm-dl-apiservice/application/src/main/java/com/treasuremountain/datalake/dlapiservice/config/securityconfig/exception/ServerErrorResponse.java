package com.treasuremountain.datalake.dlapiservice.config.securityconfig.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class ServerErrorResponse {


    // HTTP Response Status Code
    private final HttpStatus status;

    // General Error message
    private final String message;

    // Error code
    private final EdgeServerErrorCode errorCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final Date timestamp;

    protected ServerErrorResponse(final String message, final EdgeServerErrorCode errorCode, HttpStatus status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = new Date();
    }

    public static ServerErrorResponse of(final String message, final EdgeServerErrorCode errorCode, HttpStatus status) {
        return new ServerErrorResponse(message, errorCode, status);
    }

    public Integer getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public EdgeServerErrorCode getErrorCode() {
        return errorCode;
    }

    public Date getTimestamp() {
        return timestamp!=null?(Date) timestamp.clone():null;
    }
}
