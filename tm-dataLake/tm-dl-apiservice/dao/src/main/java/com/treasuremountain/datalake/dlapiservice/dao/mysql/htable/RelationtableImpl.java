package com.treasuremountain.datalake.dlapiservice.dao.mysql.htable;

import com.treasuremountain.datalake.dlapiservice.common.data.business.BusinessConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.data.htable.RelationtableConfigDto;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.DaoUtil;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.BusinessConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.RelationtableConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.BusinessConfigDo;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.RelationtableConfigDo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xun-yu.she on 6/17/2019
 */
@Component
public class RelationtableImpl {

    public List<com.treasuremountain.datalake.dlapiservice.common.data.htable.RelationtableConfigDto> selectByBusinessId(String businessId) {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        RelationtableConfigDoMapper mapper = session.getMapper(RelationtableConfigDoMapper.class);

        RelationtableConfigDoMapper relationMapper = session.getMapper(RelationtableConfigDoMapper.class);
        RelationtableConfigDo relationtableConfigDo = new RelationtableConfigDo();
        relationtableConfigDo.setBusinessId(businessId);
        relationtableConfigDo.setRelationtableIsenable(true);
        List<RelationtableConfigDo> relationtableConfigDos = relationMapper.selectSelective(relationtableConfigDo);
        session.close();
        return DaoUtil.convertDataList(relationtableConfigDos);
    }

    public Integer insert(RelationtableConfigDo record) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                RelationtableConfigDoMapper relationMapper = session.getMapper(RelationtableConfigDoMapper.class);
                Integer count = relationMapper.insert(record);
                if (count <= 0) {
                    throw new Exception("新增RelationTableConfig不成功");
                }
                session.commit();
                return count;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Integer updateByPrimaryKey(RelationtableConfigDo record) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                RelationtableConfigDoMapper relationMapper = session.getMapper(RelationtableConfigDoMapper.class);
                Integer count = relationMapper.updateByPrimaryKey(record);
                if (count <= 0) {
                    throw new Exception("修改RelationTableConfig不成功");
                }
                session.commit();
                return count;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    //软删除
    public Integer deleteByPrimaryKey(String id) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                RelationtableConfigDoMapper relationMapper = session.getMapper(RelationtableConfigDoMapper.class);
                RelationtableConfigDo relationtableDo = new RelationtableConfigDo();
                relationtableDo.setRelationtableId(id);
                relationtableDo.setRelationtableIsenable(false);
                Integer count = relationMapper.updateBybusinessIdSelective(relationtableDo);
                if (count < 1) {
                    count++;
                    //throw new Exception("删除RelationtableConfig不成功");
                }
                session.commit();
                return count;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<RelationtableConfigDto> selectSelective(RelationtableConfigDo relationtableConfigDo) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                RelationtableConfigDoMapper relationMapper = session.getMapper(RelationtableConfigDoMapper.class);
                List<RelationtableConfigDo> relationtableConfigDos = relationMapper.selectSelective(relationtableConfigDo);
                if (relationtableConfigDos != null && relationtableConfigDos.size() > 0) {
                    List<RelationtableConfigDto> relationDtos = new ArrayList<>();
                    for (RelationtableConfigDo relationDoFor :
                            relationtableConfigDos) {
                        RelationtableConfigDto relationDto = relationDoFor.toData();
                        relationDtos.add(relationDto);
                    }
                    session.commit();
                    return relationDtos;
                }
                return null;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (
                Exception ex) {
            throw ex;
        }
    }


    public List<BusinessConfigDto> selectByPrimaryKeyList(List<String> list) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {

                BusinessConfigDoMapper businessMapper = session.getMapper(BusinessConfigDoMapper.class);
                List<BusinessConfigDo> businessConfigDos = businessMapper.selectByPrimaryKeyList(list);
                if (businessConfigDos != null && businessConfigDos.size() > 0) {
                    List<BusinessConfigDto> businessConfigDtos = new ArrayList();
                    for (BusinessConfigDo businessConfigDo :
                            businessConfigDos) {
                        BusinessConfigDto businessConfigDto = new BusinessConfigDto();
                        businessConfigDto.setBusinessId(businessConfigDo.getBusinessId());
                        businessConfigDto.setBusinessName(businessConfigDo.getBusinessName());
                        businessConfigDto.setBusinessDesc(businessConfigDo.getBusinessDesc());
                        businessConfigDto.setExchangeId(businessConfigDo.getExchangeId());
                        businessConfigDto.setBusinessIsenable(businessConfigDo.getBusinessIsenable());
                        businessConfigDto.setBusinessModifiedby(businessConfigDo.getBusinessModifiedby());
                        businessConfigDto.setBusinessModifieddt(businessConfigDo.getBusinessModifieddt());
                        businessConfigDto.setBusinessCreateby(businessConfigDo.getBusinessCreateby());
                        businessConfigDto.setBusinessCreatedt(businessConfigDo.getBusinessCreatedt());
                        businessConfigDtos.add(businessConfigDto);
                    }
                    session.commit();
                    return businessConfigDtos;
                }
                return null;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (
                Exception ex) {
            throw ex;
        }
    }


}
