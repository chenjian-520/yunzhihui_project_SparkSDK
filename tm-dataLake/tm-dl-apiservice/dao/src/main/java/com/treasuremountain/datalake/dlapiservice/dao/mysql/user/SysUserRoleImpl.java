package com.treasuremountain.datalake.dlapiservice.dao.mysql.user;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.SysRoleMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.SysRole;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/6/18.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Component
public class SysUserRoleImpl {

    @Autowired
    private Dao dao;

    public List<SysRole> selectByUserid(String uid){
        try(SqlSession sqlSession = dao.sqlSessionFactory.openSession()){
            return  sqlSession.getMapper(SysRoleMapper.class).selectByUserid(uid);
        }
    }
}
