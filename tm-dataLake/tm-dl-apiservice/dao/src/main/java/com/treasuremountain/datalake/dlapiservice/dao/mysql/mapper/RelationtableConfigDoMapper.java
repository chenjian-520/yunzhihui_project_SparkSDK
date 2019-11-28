package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.RelationtableConfigDo;

import java.util.List;

public interface RelationtableConfigDoMapper {
    int deleteByPrimaryKey(String relationtableId);

    int insert(RelationtableConfigDo record);

    int insertSelective(RelationtableConfigDo record);

    RelationtableConfigDo selectByPrimaryKey(String relationtableId);

    int updateByPrimaryKeySelective(RelationtableConfigDo record);

    int updateByPrimaryKey(RelationtableConfigDo record);

    int updateBybusinessIdSelective(RelationtableConfigDo record);

    List<RelationtableConfigDo> selectSelective(RelationtableConfigDo record);

}