package com.treasuremountain.datalake.dlapiservice.dao.mysql.business;

import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.BusinessConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.ExchangeConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.BusinessConfigDo;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.ExchangeConfigDo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by xun-yu.she on 6/18/2019.
 */
@Component
public class ExchangeImpl {

    public Integer insert(ExchangeConfigDo record) throws Exception {

        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                ExchangeConfigDoMapper mapper = session.getMapper(ExchangeConfigDoMapper.class);
                //判断Name是否重复
                ExchangeConfigDo selectModel = new ExchangeConfigDo();
                selectModel.setExchangeName(record.getExchangeName());
                selectModel.setExchangeIsenable(true);
                List<ExchangeConfigDo> exchangeConfigDos = mapper.selectSelective(selectModel);
                if (exchangeConfigDos != null && exchangeConfigDos.size() > 0) {
                    throw new Exception("新增ExchangeName已存在");
                }
                Integer count = mapper.insert(record);
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


    public List<ExchangeConfigDo> selectSelective(ExchangeConfigDo record) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                ExchangeConfigDoMapper mapper = session.getMapper(ExchangeConfigDoMapper.class);
                List<ExchangeConfigDo> exchangeConfigDos = mapper.selectSelective(record);
                session.commit();
                return exchangeConfigDos;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }

    }

    public Integer updateByPrimaryKey() throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
               /* BusinessConfigDoMapper mapper = session.getMapper(BusinessConfigDoMapper.class);
                Integer count = mapper.updateByPrimaryKey(record);
                if (count <= 0) {
                    throw new Exception("修改RelationTableConfig不成功");
                } else {
                    throw new Exception("新增BusinessConfig不成功");
                }
                session.commit();
                return count;*/
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return 0;
    }

    //软删除
    public Integer deleteByPrimaryKey(String id) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                BusinessConfigDoMapper busiMapper = session.getMapper(BusinessConfigDoMapper.class);
                BusinessConfigDo businessConfigDo = new BusinessConfigDo();
                businessConfigDo.setExchangeId(id);
                businessConfigDo.setBusinessIsenable(true);
                List<BusinessConfigDo> businessConfigDos = busiMapper.selectSelective(businessConfigDo);
                if (businessConfigDos != null && businessConfigDos.size() > 0) {
                    StringBuilder strb = new StringBuilder();
                    Boolean isFirst = false;
                    for (BusinessConfigDo busiModel :
                            businessConfigDos) {
                        if (isFirst) {
                            strb.append(",");
                        }
                        strb.append("【" + busiModel.getBusinessId() + ":" + busiModel.getBusinessName() + "】");
                        if (!isFirst) isFirst = true;
                    }
                    throw new Exception("该数据转发节点已经在" + strb.toString() + "中使用，请先解除绑定");
                } else {
                    ExchangeConfigDoMapper exchMapper = session.getMapper(ExchangeConfigDoMapper.class);
                    ExchangeConfigDo exchangeConfigDo = new ExchangeConfigDo();
                    exchangeConfigDo.setExchangeId(id);
                    exchangeConfigDo.setExchangeIsenable(false);
                    Integer count = exchMapper.updateByPrimaryKeySelective(exchangeConfigDo);
                    if (count < 1) {
                        count++;
                        //throw new Exception("删除ExchangeConfig不成功");
                    }
                    session.commit();
                    return count;
                }
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }


}
