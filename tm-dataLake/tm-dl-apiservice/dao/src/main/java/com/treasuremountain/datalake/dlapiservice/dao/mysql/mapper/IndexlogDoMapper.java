package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.IndexlogDo;

import java.util.List;

public interface IndexlogDoMapper {
    int deleteByPrimaryKey(String indexlogId);

    int deleteByTableAndName(IndexlogDo record);

    int insert(IndexlogDo record);

    int insertSelective(IndexlogDo record);

    IndexlogDo selectByPrimaryKey(String indexlogId);

    List<IndexlogDo> selectAll();

    int updateByPrimaryKeySelective(IndexlogDo record);

    int updateByPrimaryKey(IndexlogDo record);

    List<IndexlogDo> selectSelective(IndexlogDo record);
}