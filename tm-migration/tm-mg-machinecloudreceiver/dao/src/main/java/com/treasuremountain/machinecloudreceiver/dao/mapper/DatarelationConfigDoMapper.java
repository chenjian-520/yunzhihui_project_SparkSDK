package com.treasuremountain.machinecloudreceiver.dao.mapper;

import com.treasuremountain.machinecloudreceiver.dao.model.DatarelationConfigDo;

import java.util.List;

public interface DatarelationConfigDoMapper {
    int deleteByPrimaryKey(String datarelationId);

    int insert(DatarelationConfigDo record);

    int insertSelective(DatarelationConfigDo record);

    DatarelationConfigDo selectByPrimaryKey(String datarelationId);

    List<DatarelationConfigDo> selectAll();

    int updateByPrimaryKeySelective(DatarelationConfigDo record);

    int updateByPrimaryKey(DatarelationConfigDo record);
}