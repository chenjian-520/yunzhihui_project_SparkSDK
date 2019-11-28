package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.HBcolumnfamilyConfigDo;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.HBtableConfigDo;

import java.util.List;

public interface HBcolumnfamilyConfigDoMapper {
    int deleteByPrimaryKey(String hbcolumnfamilyId);

    int insert(HBcolumnfamilyConfigDo record);

    int insertSelective(HBcolumnfamilyConfigDo record);

    HBcolumnfamilyConfigDo selectByPrimaryKey(String hbcolumnfamilyId);

    List<HBcolumnfamilyConfigDo> selectByhbtableId(String hbtableId);

    int updateByPrimaryKeySelective(HBcolumnfamilyConfigDo record);

    int updateByPrimaryKey(HBcolumnfamilyConfigDo record);

    List<HBcolumnfamilyConfigDo> selectSelective(HBcolumnfamilyConfigDo record);

    List<HBcolumnfamilyConfigDo> selectByPrimaryKeyList(List<String> list);

    int updateSelective(HBcolumnfamilyConfigDo record);

}