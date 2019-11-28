package com.treasuremountain.datalake.dlapiservice.config.securityconfig.exception;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/3/1.
 * Company: Foxconn
 * Project: MaxIoT
 */
public class CustomAsyncRequestTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 8754629185999484614L;

    public CustomAsyncRequestTimeoutException(String uri){
        super(uri);
    }
}
