package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.TablelogDo;
import org.springframework.stereotype.Component;

import java.util.List;

public interface TablelogDoMapper {
    int deleteByPrimaryKey(String tablelogId);

    int insert(TablelogDo record);

    int insertSelective(TablelogDo record);

    TablelogDo selectByPrimaryKey(String tablelogId);

    List<TablelogDo> selectAll();

    int updateByPrimaryKeySelective(TablelogDo record);

    int updateByPrimaryKey(TablelogDo record);

    List<TablelogDo> selectSelective(TablelogDo record);
}