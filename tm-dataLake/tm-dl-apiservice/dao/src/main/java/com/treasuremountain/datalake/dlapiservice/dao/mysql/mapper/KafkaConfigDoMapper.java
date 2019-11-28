package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.KafkaConfigDo;

import java.util.List;

public interface KafkaConfigDoMapper {
    int deleteByPrimaryKey(String id);

    int insert(KafkaConfigDo record);

    int insertSelective(KafkaConfigDo record);

    KafkaConfigDo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(KafkaConfigDo record);

    int updateByPrimaryKey(KafkaConfigDo record);

    List<KafkaConfigDo> selectAll();
}