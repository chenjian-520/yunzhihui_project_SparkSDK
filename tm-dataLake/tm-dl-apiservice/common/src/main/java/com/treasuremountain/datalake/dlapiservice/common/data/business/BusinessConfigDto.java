package com.treasuremountain.datalake.dlapiservice.common.data.business;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.RelationtableConfigDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by gerryzhao on 10/20/2018.
 */
@Data
public class BusinessConfigDto {
    //前端给
    private String businessId;

    private String businessName;

    private String businessDesc;

    //前端给
    private String exchangeId;

    private String exchangeName;

    private Boolean businessIsenable;
    //add:in
    private String businessModifiedby;

    private Date businessModifieddt;
    //add:in
    private String businessCreateby;

    private Date businessCreatedt;

    private List<RelationtableConfigDto> relationtableConfigs;
}
