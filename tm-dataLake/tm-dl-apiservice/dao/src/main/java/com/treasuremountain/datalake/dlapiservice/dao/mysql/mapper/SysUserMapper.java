package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.SysUserDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(String userAccount);

    int insert(SysUserDo record);

    int insertSelective(SysUserDo record);

    SysUserDo selectByPrimaryKey(String userAccount);

    SysUserDo selectById(@Param("uid") String uid);

    int updateByPrimaryKeySelective(SysUserDo record);

    int updateByPrimaryKey(SysUserDo record);

    List<SysUserDo> selectTenatChild(String pid);
}