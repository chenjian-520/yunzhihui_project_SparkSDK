package com.treasuremountain.datalake.dlapiservice.dao.mysql.user;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.SysUserMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.SysUserDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description：用户管理相关API
 * <p>
 * Created by ref.tian on 2018/6/4.
 * Company: Maxnerva
 * Project: polaris
 */
@Component
@Slf4j
public class SysUserImpl {
    @Autowired
    private Dao dao;

    public int deleteByPrimaryKey(String acount) {
        try (SqlSession sqlSession = dao.sqlSessionFactory.openSession()) {
            int i = sqlSession.getMapper(SysUserMapper.class).deleteByPrimaryKey(acount);
            sqlSession.commit();
            sqlSession.close();
            return i;
        }
    }


    public int insert(SysUserDo record) {
        try (SqlSession sqlSession = dao.sqlSessionFactory.openSession()) {
            int i = sqlSession.getMapper(SysUserMapper.class).insert(record);
            sqlSession.commit();
            sqlSession.close();
            return i;
        }

    }

    public int insertSelective(SysUserDo record) {
        return 0;
    }

    public SysUserDo selectByPrimaryKey(String acount) {
        SysUserDo sysUserDo = null;
        try (SqlSession sqlSession = dao.sqlSessionFactory.openSession()) {
            sysUserDo = sqlSession.getMapper(SysUserMapper.class).selectByPrimaryKey(acount);
            sqlSession.close();
            return sysUserDo;
        }
    }

    public int updateByPrimaryKeySelective(SysUserDo record) {
        try (SqlSession sqlSession = dao.sqlSessionFactory.openSession()) {
            int i = sqlSession.getMapper(SysUserMapper.class).updateByPrimaryKeySelective(record);
            sqlSession.commit();
            sqlSession.close();
            return i;
        }
    }

    public int updateByPrimaryKey(SysUserDo record) {
        try (SqlSession sqlSession = dao.sqlSessionFactory.openSession()) {
            int i = sqlSession.getMapper(SysUserMapper.class).updateByPrimaryKey(record);
            sqlSession.commit();
            sqlSession.close();
            return i;
        }
    }

    public SysUserDo selectByUserAccount(String account) {
        SysUserDo sysUserDo = null;
        try (SqlSession sqlSession = dao.sqlSessionFactory.openSession()) {
            sysUserDo = sqlSession.getMapper(SysUserMapper.class).selectByPrimaryKey(account);
            sqlSession.close();
            return sysUserDo;
        } finally {
            sysUserDo = null;
        }
    }

    public List<SysUserDo> selectTenatChild(String pid) {
        try (SqlSession sqlSession = dao.sqlSessionFactory.openSession()) {
            List<SysUserDo> i = sqlSession.getMapper(SysUserMapper.class).selectTenatChild(pid);
            sqlSession.close();
            return i;
        }
    }


    public SysUserDo selectById(String uid) {
        try (SqlSession sqlSession = dao.sqlSessionFactory.openSession()) {
            SysUserDo i = sqlSession.getMapper(SysUserMapper.class).selectById(uid);
            sqlSession.close();
            return i;
        }
    }



}
