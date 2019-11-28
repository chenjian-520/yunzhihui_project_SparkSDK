package com.treasuremountain.datalake.dlapiservice.common.data.query;

import lombok.Data;

import java.util.Date;

/**
 * Created by gerryzhao on 10/20/2018.
 */
@Data
public class ExchangeConfigDto {
    //add:local
    private String exchangeId;

    private String exchangeName;

    private Boolean exchangeIsenable;

    //前端给
    private String exchangeModifiedby;

    private Date exchangeModifieddt;
    //前端给
    private String exchangeCreateby;

    private Date exchangeCreatedt;

}
