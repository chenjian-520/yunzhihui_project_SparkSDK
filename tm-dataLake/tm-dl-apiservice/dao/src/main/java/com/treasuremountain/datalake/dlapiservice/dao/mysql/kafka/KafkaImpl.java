package com.treasuremountain.datalake.dlapiservice.dao.mysql.kafka;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.KafkaConfigDoMapper;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.KafkaConfigDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @program: dlapiservice->KafkaImpl
 * @description: kafkaConfig 实现类
 * @author: Axin
 * @create: 2019-11-16 08:22
 **/
@Component
@Slf4j
public class KafkaImpl {
    private Gson gson=new Gson();

    public List<KafkaConfigDo> selectAll(){
        if (Dao.sqlSessionFactory == null) {
            try {
                Dao dao = new Dao();
                dao.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            List<KafkaConfigDo> kafkaConfigDos = session.getMapper(KafkaConfigDoMapper.class).selectAll();
            session.close();
            return kafkaConfigDos;
        }
    }
}
