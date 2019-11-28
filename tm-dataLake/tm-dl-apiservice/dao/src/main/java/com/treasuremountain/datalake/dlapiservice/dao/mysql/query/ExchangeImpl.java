package com.treasuremountain.datalake.dlapiservice.dao.mysql.query;

import com.treasuremountain.datalake.dlapiservice.common.data.query.ExchangeConfigDto;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.DaoUtil;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.ExchangeConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.ExchangeConfigDo;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by gerryzhao on 10/20/2018.
 */
public class ExchangeImpl {
    public ExchangeConfigDto selectByPrimaryKey(String exchangeId) {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        ExchangeConfigDoMapper mapper = session.getMapper(ExchangeConfigDoMapper.class);
        ExchangeConfigDo configDo = mapper.selectByPrimaryKey(exchangeId);

        session.close();

        return DaoUtil.getData(configDo);
    }
}
