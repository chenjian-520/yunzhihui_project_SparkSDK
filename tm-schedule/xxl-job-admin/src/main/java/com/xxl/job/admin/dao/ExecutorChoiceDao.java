package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.ExecutorChoiceDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExecutorChoiceDao {
    int deleteByPrimaryKey(String executorchoiceConfigId);

    int insert(ExecutorChoiceDo record);

    int insertSelective(ExecutorChoiceDo record);

    ExecutorChoiceDo selectByPrimaryKey(String executorchoiceConfigId);

    List<ExecutorChoiceDo> selectAll();

    int updateByPrimaryKeySelective(ExecutorChoiceDo record);

    int updateByPrimaryKey(ExecutorChoiceDo record);
}