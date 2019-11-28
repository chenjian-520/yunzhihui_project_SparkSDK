package com.treasuremountain.datalake.dlapiservice.common.data.htable;

import lombok.Data;

import java.util.Date;

/**
 * Created by gerryzhao on 10/21/2018.
 */
@Data
public class HBcolumnConfigDto {
    //add:local
    private String hbcolumnId;
    //add:local
    private String hbcolumnfamilyId;

    private String hbcolumnName;

    private String hbcolumnType;

    private Boolean hbcolumnIsenable;

    private Boolean hbcolumnIsindex;

    private String hbcolumnModifiedby;

    private Date hbcolumnModifieddt;

    private String hbcolumnCreateby;

    private Date hbcolumnCreatedt;

    private String hbcolumnDesc;

    private String status;// 标识当前数据是add新增/delete删除还是edit修改

}
