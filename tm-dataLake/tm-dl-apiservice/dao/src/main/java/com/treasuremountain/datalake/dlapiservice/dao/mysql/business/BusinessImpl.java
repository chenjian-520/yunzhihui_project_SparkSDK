package com.treasuremountain.datalake.dlapiservice.dao.mysql.business;

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

import java.util.*;

/**
 * Created by gerryzhao on 10/20/2018.
 * Edit by xun-yu.she on 6/17/2019
 */
@Component
public class BusinessImpl {

    public List<BusinessConfigDto> findAllBusiness() {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        BusinessConfigDoMapper mapper = session.getMapper(BusinessConfigDoMapper.class);
        List<BusinessConfigDo> doList = mapper.selectAll();

        session.close();

        List<BusinessConfigDto> dtoList = DaoUtil.convertDataList(doList);

        return dtoList;
    }


    //region 重用代码

    //businessName检查
    public void businessNameCheck(BusinessConfigDoMapper busMapper, String businessName) throws Exception {
        BusinessConfigDo selectModel = new BusinessConfigDo();
        selectModel.setBusinessName(businessName);
        selectModel.setBusinessIsenable(true);
        List<BusinessConfigDo> businessConfigDos = busMapper.selectSelective(selectModel);
        if (businessConfigDos != null && businessConfigDos.size() > 0) {
            throw new Exception("新增BusinessName已存在");
        }
    }

    //relationtable检查
    public void relationtableCheck(RelationtableConfigDoMapper relationMapper, RelationtableConfigDo relationtableDo, String status) throws Exception {
        RelationtableConfigDo selectModel = new RelationtableConfigDo();
        selectModel.setBusinessId(relationtableDo.getBusinessId());
        selectModel.setHbtableId(relationtableDo.getHbtableId());
        selectModel.setHbcolumnfamilyId(relationtableDo.getHbcolumnfamilyId());
        selectModel.setHbcolumnId(relationtableDo.getHbcolumnId());
        selectModel.setMsgkey(relationtableDo.getMsgkey());
        selectModel.setRelationtableIsenable(true);
        List<RelationtableConfigDo> relationtableConfigDos = relationMapper.selectSelective(selectModel);
        switch (status) {
            case "add":
                if (relationtableConfigDos != null && relationtableConfigDos.size() > 0) {
                    throw new Exception("新增BusinessId&HbtableId&HbcolumnfamilyId&HbcolumnId&Msgkey已存在");
                }
                break;
            case "edit":
                if (relationtableConfigDos != null && relationtableConfigDos.size() > 1) {
                    throw new Exception("修改BusinessId&HbtableId&HbcolumnfamilyId&HbcolumnId&Msgkey已存在");
                }
                break;
            default:
                break;

        }
    }

    //endregion


    public Integer insert(BusinessConfigDto record) throws Exception {

        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                BusinessConfigDoMapper mapper = session.getMapper(BusinessConfigDoMapper.class);
                //判断是否有相同的业务名称
                businessNameCheck(mapper, record.getBusinessName());

                BusinessConfigDo businessConfigDo = new BusinessConfigDo();
                businessConfigDo.setBusinessId(record.getBusinessId());
                businessConfigDo.setBusinessName(record.getBusinessName());
                businessConfigDo.setBusinessDesc(record.getBusinessDesc());
                businessConfigDo.setExchangeId(record.getExchangeId());
                businessConfigDo.setBusinessIsenable(true);
                businessConfigDo.setBusinessModifiedby(record.getBusinessModifiedby());
                businessConfigDo.setBusinessModifieddt(new Date());
                businessConfigDo.setBusinessCreateby(record.getBusinessModifiedby());
                businessConfigDo.setBusinessCreatedt(new Date());
                Integer count = mapper.insert(businessConfigDo);
                List<RelationtableConfigDto> relationtableConfigs = record.getRelationtableConfigs();
                if (count > 0) {
                    if (relationtableConfigs != null && relationtableConfigs.size() > 0) {
                        //继续添加relationtable_config
                        RelationtableConfigDoMapper relationMapper = session.getMapper(RelationtableConfigDoMapper.class);
                        count = 0;
                        for (RelationtableConfigDto relationDto :
                                relationtableConfigs) {
                            RelationtableConfigDo relationtableDo = new RelationtableConfigDo();
                            relationtableDo.setRelationtableId(UUID.randomUUID().toString());
                            relationtableDo.setBusinessId(record.getBusinessId());
                            relationtableDo.setHbtableId(relationDto.getHbtableId());
                            relationtableDo.setMsgkey(relationDto.getMsgkey());
                            relationtableDo.setHbcolumnfamilyId(relationDto.getHbcolumnfamilyId());
                            relationtableDo.setHbcolumnId(relationDto.getHbcolumnId());
                            relationtableDo.setRelationtableIsenable(true);
                            relationtableDo.setRelationtableModifiedby(record.getBusinessModifiedby());
                            relationtableDo.setRelationtableModifieddt(new Date());
                            relationtableDo.setRelationtableCreateby(record.getBusinessCreateby());
                            relationtableDo.setRelationtableCreatedt(new Date());
                            relationtableCheck(relationMapper, relationtableDo, "add");
                            count = relationMapper.insert(relationtableDo);
                            if (count <= 0) {
                                throw new Exception("新增RelationTableConfig不成功");
                            }
                        }
                    }
                } else {
                    throw new Exception("新增BusinessConfig不成功");
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

    public Integer updateByPrimaryKey(BusinessConfigDto record) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                BusinessConfigDoMapper mapper = session.getMapper(BusinessConfigDoMapper.class);
                //判断是否存在
                BusinessConfigDo selectModel = new BusinessConfigDo();
                selectModel.setBusinessId(record.getBusinessId());
                selectModel.setBusinessIsenable(true);
                List<BusinessConfigDo> businessConfigDos = mapper.selectSelective(selectModel);
                if (businessConfigDos == null || businessConfigDos.size() <= 0) {
                    throw new Exception("BusinessConfig不存在");
                }
                //判断修改名字后是否存在
                if (!record.getBusinessName().equals(businessConfigDos.get(0).getBusinessName())) {
                    //判断是否有相同的业务名称
                    businessNameCheck(mapper, record.getBusinessName());
                }
                BusinessConfigDo businessConfigDo = new BusinessConfigDo();
                businessConfigDo.setBusinessId(record.getBusinessId());
                businessConfigDo.setBusinessName(record.getBusinessName());
                businessConfigDo.setBusinessDesc(record.getBusinessDesc());
                businessConfigDo.setExchangeId(record.getExchangeId());
                businessConfigDo.setBusinessIsenable(true);
                businessConfigDo.setBusinessModifiedby(record.getBusinessModifiedby());
                businessConfigDo.setBusinessModifieddt(new Date());
                Integer count = mapper.updateByPrimaryKey(businessConfigDo);
                RelationtableConfigDoMapper relationMapper = session.getMapper(RelationtableConfigDoMapper.class);
                if (count > 0) {
                    //继续添加relationtable_config
                    List<RelationtableConfigDto> relationtableConfigs = record.getRelationtableConfigs();
                    if (relationtableConfigs != null && relationtableConfigs.size() > 0) {
                        for (RelationtableConfigDto relationDto :
                                relationtableConfigs) {
                            RelationtableConfigDo relationtableDo = new RelationtableConfigDo();
                            relationtableDo.setRelationtableId(relationDto.getRelationtableId());
                            relationtableDo.setBusinessId(record.getBusinessId());
                            relationtableDo.setHbtableId(relationDto.getHbtableId());
                            relationtableDo.setMsgkey(relationDto.getMsgkey());
                            relationtableDo.setHbcolumnfamilyId(relationDto.getHbcolumnfamilyId());
                            relationtableDo.setHbcolumnId(relationDto.getHbcolumnId());
                            relationtableDo.setRelationtableIsenable(true);
                            relationtableDo.setRelationtableModifiedby(record.getBusinessModifiedby());
                            relationtableDo.setRelationtableModifieddt(new Date());

                            if (relationDto.getStatus() == null || relationDto.getStatus().isEmpty()) {
                                relationDto.setStatus("");
                            } else {
                                relationDto.setStatus(relationDto.getStatus().toLowerCase());
                            }
                            switch (relationDto.getStatus()) {
                                case "add":
                                    //需要增加判断是否数据正常，目前没有控制
                                    relationtableCheck(relationMapper, relationtableDo, relationDto.getStatus());
                                    relationtableDo.setRelationtableId(UUID.randomUUID().toString());
                                    relationtableDo.setRelationtableCreateby(record.getBusinessModifiedby());
                                    relationtableDo.setRelationtableCreatedt(new Date());
                                    count = relationMapper.insert(relationtableDo);
                                    break;
                                case "edit":
                                    //需要判断修改的数据是否出现存在       //判断数据是否大于1
                                    count = relationMapper.updateByPrimaryKeySelective(relationtableDo);
                                    relationtableCheck(relationMapper, relationtableDo, relationDto.getStatus());
                                    break;
                                case "delete":
                                    relationtableDo.setRelationtableIsenable(false);
                                    count = relationMapper.updateByPrimaryKey(relationtableDo);
                                    break;
                                case "":
                                    count++; //不做处理
                                    break;
                                default:
                                    throw new Exception("Status不正确:" + relationDto.getStatus());
                            }
                            if (count <= 0) {
                                throw new Exception("修改RelationTableConfig不成功");
                            }
                        }
                    }
                } else {
                    throw new Exception("新增BusinessConfig不成功");
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
                relationtableDo.setBusinessId(id);
                relationtableDo.setRelationtableIsenable(false);
                Integer count = relationMapper.updateBybusinessIdSelective(relationtableDo);
                if (count < 1) {
                    count++;
                    //throw new Exception("删除RelationtableConfig不成功");
                }
                BusinessConfigDoMapper mapper = session.getMapper(BusinessConfigDoMapper.class);
                BusinessConfigDo businessDo = new BusinessConfigDo();
                businessDo.setBusinessId(id);
                businessDo.setBusinessIsenable(false);
                count = mapper.updateByPrimaryKeySelective(businessDo);
                if (count < 1) {
                    count++;
                    //throw new Exception("删除BusinessConfig不成功");
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

    public BusinessConfigDto selectByPrimaryKey(String businessId) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                BusinessConfigDoMapper businessMapper = session.getMapper(BusinessConfigDoMapper.class);
                BusinessConfigDo businessConfigDo = businessMapper.selectByPrimaryKey(businessId);
                if (businessConfigDo != null) {
                    BusinessConfigDto businessConfigDto = new BusinessConfigDto();
                    businessConfigDto.setBusinessId(businessConfigDo.getBusinessId());
                    businessConfigDto.setBusinessName(businessConfigDo.getBusinessName());
                    businessConfigDto.setBusinessDesc(businessConfigDo.getBusinessDesc());
                    businessConfigDto.setExchangeId(businessConfigDo.getExchangeId());
                    businessConfigDto.setExchangeName(businessConfigDo.getExchangeName());
                    businessConfigDto.setBusinessIsenable(businessConfigDo.getBusinessIsenable());
                    businessConfigDto.setBusinessModifiedby(businessConfigDo.getBusinessModifiedby());
                    businessConfigDto.setBusinessModifieddt(businessConfigDo.getBusinessModifieddt());
                    businessConfigDto.setBusinessCreateby(businessConfigDo.getBusinessCreateby());
                    businessConfigDto.setBusinessCreatedt(businessConfigDo.getBusinessCreatedt());
                    RelationtableConfigDoMapper relationMapper = session.getMapper(RelationtableConfigDoMapper.class);
                    RelationtableConfigDo relationtableConfigDo = new RelationtableConfigDo();
                    relationtableConfigDo.setBusinessId(businessId);
                    relationtableConfigDo.setRelationtableIsenable(true);
                    List<RelationtableConfigDo> relationtableConfigDos = relationMapper.selectSelective(relationtableConfigDo);
                    if (relationtableConfigDos != null && relationtableConfigDos.size() > 0) {
                        List<RelationtableConfigDto> relationDtos = new ArrayList<>();
                        for (RelationtableConfigDo relationDo :
                                relationtableConfigDos) {
                            RelationtableConfigDto relationDto = new RelationtableConfigDto();
                            relationDto.setRelationtableId(relationDo.getRelationtableId());
                            relationDto.setBusinessId(relationDo.getBusinessId());
                            relationDto.setBusinessName(relationDo.getBusinessName());
                            relationDto.setHbtableId(relationDo.getHbtableId());
                            relationDto.setHbtableName(relationDo.getHbtableName());
                            relationDto.setMsgkey(relationDo.getMsgkey());
                            relationDto.setHbcolumnfamilyId(relationDo.getHbcolumnfamilyId());
                            relationDto.setHbcolumnId(relationDo.getHbcolumnId());
                            relationDto.setRelationtableModifiedby(relationDo.getRelationtableModifiedby());
                            relationDtos.add(relationDto);
                        }
                        businessConfigDto.setRelationtableConfigs(relationDtos);
                    }
                    session.commit();
                    return businessConfigDto;
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
                        businessConfigDto.setExchangeName(businessConfigDo.getExchangeName());
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
