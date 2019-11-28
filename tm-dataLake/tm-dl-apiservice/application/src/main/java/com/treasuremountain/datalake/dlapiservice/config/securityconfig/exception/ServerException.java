package com.treasuremountain.datalake.dlapiservice.config.securityconfig.exception;


public class ServerException extends Exception {

    private static final long serialVersionUID = 1L;

    private EdgeServerErrorCode errorCode;

    public ServerException(){
        super();
    }

    public ServerException(EdgeServerErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ServerException(String message, EdgeServerErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServerException(String message, Throwable cause, EdgeServerErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ServerException(Throwable cause, EdgeServerErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public EdgeServerErrorCode getErrorCode() {
        return errorCode;
    }

}
