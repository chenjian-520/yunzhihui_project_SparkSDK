package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    List<SysRole> selectByUserid(@Param("uid") String uid);
}