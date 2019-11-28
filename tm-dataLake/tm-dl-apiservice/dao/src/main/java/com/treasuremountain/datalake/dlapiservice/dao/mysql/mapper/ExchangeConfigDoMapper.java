package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.ExchangeConfigDo;

import java.util.List;

public interface ExchangeConfigDoMapper {
    int deleteByPrimaryKey(String exchangeId);

    int insert(ExchangeConfigDo record);

    int insertSelective(ExchangeConfigDo record);

    ExchangeConfigDo selectByPrimaryKey(String exchangeId);

    int updateByPrimaryKeySelective(ExchangeConfigDo record);

    int updateByPrimaryKey(ExchangeConfigDo record);

    List<ExchangeConfigDo> selectSelective(ExchangeConfigDo record);

    List<ExchangeConfigDo> selectByPrimaryKeyList(List<String> list);
}