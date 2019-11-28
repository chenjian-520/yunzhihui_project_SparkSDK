package com.treasuremountain.datalake.dlapiservice.dao.mysql.htable;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBcolumnConfigDto;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.DaoUtil;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.HBcolumnConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.HBcolumnConfigDo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by gerryzhao on 10/21/2018.
 */
@Component
public class HBcolumnImpl {
    public List<HBcolumnConfigDto> selectByhbcolumnfamilyId(String hbcolumnfamilyId) {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        HBcolumnConfigDoMapper mapper = session.getMapper(HBcolumnConfigDoMapper.class);
        List<HBcolumnConfigDo> doList = mapper.selectByhbcolumnfamilyId(hbcolumnfamilyId);

        session.close();

        return DaoUtil.convertDataList(doList);
    }
}
