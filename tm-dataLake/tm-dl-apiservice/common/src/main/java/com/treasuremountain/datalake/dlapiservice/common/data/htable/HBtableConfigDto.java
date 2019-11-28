package com.treasuremountain.datalake.dlapiservice.common.data.htable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex.IndexlogDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "hbtableId")
@JsonIgnoreProperties(value={"handler"})
public class HBtableConfigDto {
    //Add:in
    private String hbtableId;

    private String hbtableName;

    private Boolean hbtableIscompression;

    private String hbtableCompressionname;

    private Boolean hbtableIssplit;

    private String hbtableSplitinfo;

    private String hbtableDesc;

    private Boolean hbtableIstablesegment;

    private int hbtablesegmenttime;

    private int hbtableretentiontime;

    private String hbcurrenttablename;

    private Boolean hbtableIstwoLevelIndex;

    private int hbindexsegmenttime;

    private int hbindexretentiontime;

    private String hbcurrentindexname;

    private Boolean hbtableIsenable;

    private Boolean initResult;

    //add:in first
    private String hbtableModifiedby;

    private Date hbtableModifieddt;

    //add:in first
    private String hbtableCreateby;

    private Date hbtableCreatedt;

    private List<HBcolumnfamilyConfigDto> columnfamilyConfigDtos;

    private List<IndexlogDto> indexlogDtos;
}
