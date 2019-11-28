package com.treasuremountain.datalake.dlapiservice.dao.mysql.htable;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBcolumnfamilyConfigDto;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.DaoUtil;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.HBcolumnfamilyConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.HBcolumnfamilyConfigDo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by gerryzhao on 10/21/2018.
 */
@Component
public class HBcolumnfamilyImpl {
    public List<HBcolumnfamilyConfigDto> selectByhbtableId(String hbtableId) {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        HBcolumnfamilyConfigDoMapper mapper = session.getMapper(HBcolumnfamilyConfigDoMapper.class);
        List<HBcolumnfamilyConfigDo> doList  = mapper.selectByhbtableId(hbtableId);

        session.close();

        return DaoUtil.convertDataList(doList);
    }
}
