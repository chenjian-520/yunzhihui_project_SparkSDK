package com.treasuremountain.datalake.dlapiservice.common.data.htable;

import lombok.Data;

import java.util.Date;

@Data
public class RelationtableConfigDto {

    //add:local
    private String relationtableId;
    //add:local
    private String businessId;
    //这是通过leftJoin查询用的
    private String businessName;
    //add:in
    private String hbtableId;
    //不这是通过leftJoin查询用的
    private String hbtableName;
    private String msgkey;
    //add:in
    private String hbcolumnfamilyId;
    //不这是通过leftJoin查询用的
    private String hbcolumnfamilyName;
    //add:in
    private String hbcolumnId;
    //不这是通过leftJoin查询用的
    private String hbcolumnName;
    private String hbcolumnType;
    private Boolean relationtableIsenable;
    private String relationtableModifiedby;
    private Date relationtableModifieddt;
    private String relationtableCreateby;
    private Date relationtableCreatedt;
    private String status;// 标识当前数据是:add新增/delete删除/edit修改
}