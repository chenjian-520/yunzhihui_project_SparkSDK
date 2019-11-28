package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;


import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.SysUserRole;

public interface SysUserRoleMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);
}