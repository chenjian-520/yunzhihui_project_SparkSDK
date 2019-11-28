package com.treasuremountain.datalake.dlapiservice.dao.mysql.twolevelindex;

import com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex.IndexlogDto;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.DaoUtil;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.IndexlogDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.IndexlogDo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class IndexlogImpl {

    public List<IndexlogDto> selectAll() {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        IndexlogDoMapper mapper = session.getMapper(IndexlogDoMapper.class);
        List<IndexlogDo> doList = mapper.selectAll();

        session.close();

        return DaoUtil.convertDataList(doList);
    }

    public void addIndexlog(String htid, String indexName) {

        IndexlogDo indexlogDo = new IndexlogDo();
        indexlogDo.setIndexlogId(UUID.randomUUID().toString());
        indexlogDo.setHbtableId(htid);
        indexlogDo.setIndexlogName(indexName);
        indexlogDo.setIndexlogCreatetime(new Date());

        SqlSession session = Dao.sqlSessionFactory.openSession();

        IndexlogDoMapper mapper = session.getMapper(IndexlogDoMapper.class);
        mapper.insertSelective(indexlogDo);

        session.commit();
        session.close();
    }

    public void deleteIndexlog(String hbtableId, String indexlogName) {

        IndexlogDo indexlogDo = new IndexlogDo();
        indexlogDo.setHbtableId(hbtableId);
        indexlogDo.setIndexlogName(indexlogName);

        SqlSession session = Dao.sqlSessionFactory.openSession();

        IndexlogDoMapper mapper = session.getMapper(IndexlogDoMapper.class);
        mapper.deleteByTableAndName(indexlogDo);

        session.commit();
        session.close();
    }

    public List<IndexlogDto> selectSelective(IndexlogDo record) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                IndexlogDoMapper mapper = session.getMapper(IndexlogDoMapper.class);
                List<IndexlogDo> indexlogDos = mapper.selectSelective(record);
                List<IndexlogDto> indexlogDtos=new ArrayList<>();
                for (IndexlogDo indexFor:
                        indexlogDos) {
                    IndexlogDto indexlogDto=new IndexlogDto();
                    indexlogDto.setHbtableId(indexFor.getHbtableId());
                    indexlogDto.setIndexlogId(indexFor.getIndexlogId());
                    indexlogDto.setIndexlogName(indexFor.getIndexlogName());
                    indexlogDto.setIndexlogCreatetime(indexFor.getIndexlogCreatetime());
                    indexlogDtos.add(indexlogDto);
                }
                session.commit();
                return indexlogDtos;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }

    }

}
