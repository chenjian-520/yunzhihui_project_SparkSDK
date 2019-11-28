package com.treasuremountain.datalake.dlapiservice.dao.mysql.htable;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.TableLogDto;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.DaoUtil;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.ExchangeConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.TablelogDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.ExchangeConfigDo;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.TablelogDo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class TableLogImpl {
    public List<TableLogDto> selectAll() {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        TablelogDoMapper mapper = session.getMapper(TablelogDoMapper.class);
        List<TablelogDo> doList = mapper.selectAll();

        session.close();

        return DaoUtil.convertDataList(doList);
    }

    public void addTableLog(String htid, String tableName) {
        TablelogDo tablelogDo = new TablelogDo();
        tablelogDo.setTablelogId(UUID.randomUUID().toString());
        tablelogDo.setHbtableId(htid);
        tablelogDo.setTablelogName(tableName);
        tablelogDo.setTablelogCreatetime(new Date());

        SqlSession session = Dao.sqlSessionFactory.openSession();
        TablelogDoMapper mapper = session.getMapper(TablelogDoMapper.class);
        mapper.insertSelective(tablelogDo);

        session.commit();
        session.close();
    }


    public List<TableLogDto> selectSelective(TablelogDo record) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                TablelogDoMapper mapper = session.getMapper(TablelogDoMapper.class);
                List<TablelogDo> tablelogDos = mapper.selectSelective(record);
                List<TableLogDto> tableLogDtos=new ArrayList<>();
                for (TablelogDo tableFor:
                tablelogDos) {
                    TableLogDto tableLogDto=new TableLogDto();
                    tableLogDto.setHbtableId(tableFor.getHbtableId());
                    tableLogDto.setTablelogId(tableFor.getTablelogId());
                    tableLogDto.setTablelogName(tableFor.getTablelogName());
                    tableLogDto.setTablelogCreatetime(tableFor.getTablelogCreatetime());
                    tableLogDtos.add(tableLogDto);
                }
                session.commit();
                return tableLogDtos;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }

    }

}
