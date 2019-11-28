package com.treasuremountain.machinecloudreceiver.dao.datarelation;

import com.treasuremountain.machinecloudreceiver.common.data.DatarelationConfigDto;
import com.treasuremountain.machinecloudreceiver.dao.Dao;
import com.treasuremountain.machinecloudreceiver.dao.DaoUtil;
import com.treasuremountain.machinecloudreceiver.dao.mapper.DatarelationConfigDoMapper;
import com.treasuremountain.machinecloudreceiver.dao.model.DatarelationConfigDo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatarelationImpl {
    public List<DatarelationConfigDto> findAllDatarelation(){
        SqlSession session = Dao.sqlSessionFactory.openSession();
        DatarelationConfigDoMapper mapper = session.getMapper(DatarelationConfigDoMapper.class);

        List<DatarelationConfigDo> doList = mapper.selectAll();

        session.close();

        List<DatarelationConfigDto> dtoList = DaoUtil.convertDataList(doList);

        return dtoList;
    }
}
