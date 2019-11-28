package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.ThirdpartyClassDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ThirdpartyClassDao {
    int deleteByPrimaryKey(String thirdpartyclassConfigId);

    int insert(ThirdpartyClassDo record);

    int insertSelective(ThirdpartyClassDo record);

    ThirdpartyClassDo selectByPrimaryKey(String thirdpartyclassConfigId);

    List<ThirdpartyClassDo> selectAll();

    int updateByPrimaryKeySelective(com.xxl.job.admin.core.model.ThirdpartyClassDo record);

    int updateByPrimaryKey(com.xxl.job.admin.core.model.ThirdpartyClassDo record);
}