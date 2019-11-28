package com.treasuremountain.datalake.dlapiservice.common.data.htable;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by gerryzhao on 10/21/2018.
 * Edit by xun-yu.she on 06/20/2019.
 */
@Data
public class HBcolumnfamilyConfigDto {
    //add:local
    private String hbcolumnfamilyId;
    //add:in
    private String hbtableId;

    private String hbcolumnfamilyName;

    private Boolean hbcolumnfamilyIsenable;

    private String hbcolumnfamilyModifiedby;

    private Date hbcolumnfamilyModifieddt;

    private String hbcolumnfamilyCreateby;

    private Date hbcolumnfamilyCreatedt;

    private String status;// 标识当前数据是add新增/delete删除还是edit修改/空或者或者none就是无修改

    private List<HBcolumnConfigDto>  columnConfigDtos;
}
