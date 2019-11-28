package com.treasuremountain.datalake.dlapiservice.common.data.htable;

import lombok.Data;

import java.util.Date;

@Data
public class TableLogDto {

    private String tablelogId;

    private String hbtableId;

    private String tablelogName;

    private Date tablelogCreatetime;


}
