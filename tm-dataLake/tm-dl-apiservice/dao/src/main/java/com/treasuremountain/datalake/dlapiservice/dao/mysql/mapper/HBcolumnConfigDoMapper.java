package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.HBcolumnConfigDo;

import java.util.List;

public interface HBcolumnConfigDoMapper {
    int deleteByPrimaryKey(String hbcolumnId);

    int insert(HBcolumnConfigDo record);

    int insertSelective(HBcolumnConfigDo record);

    HBcolumnConfigDo selectByPrimaryKey(String hbcolumnId);

    List<HBcolumnConfigDo> selectByhbcolumnfamilyId(String hbcolumnfamilyId);

    int updateByPrimaryKeySelective(HBcolumnConfigDo record);

    int updateByPrimaryKey(HBcolumnConfigDo record);

    List<HBcolumnConfigDo> selectSelective(HBcolumnConfigDo record);

    List<HBcolumnConfigDo> selectByPrimaryKeyList(List<String> list);

    int updateSelective(HBcolumnConfigDo record);

    List<HBcolumnConfigDo> selectByhbcolumnfamilyIdList(List<String> list);

}