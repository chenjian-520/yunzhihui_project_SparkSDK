package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.BusinessConfigDo;

import java.util.List;

public interface BusinessConfigDoMapper {
    int deleteByPrimaryKey(String businessId);

    List<BusinessConfigDo> selectAll();

    int insert(BusinessConfigDo record);

    int insertSelective(BusinessConfigDo record);

    BusinessConfigDo selectByPrimaryKey(String businessId);

    int updateByPrimaryKeySelective(BusinessConfigDo record);

    int updateByPrimaryKey(BusinessConfigDo record);

    List<BusinessConfigDo> selectSelective(BusinessConfigDo record);

    List<BusinessConfigDo> selectByPrimaryKeyList(List<String> list);



}