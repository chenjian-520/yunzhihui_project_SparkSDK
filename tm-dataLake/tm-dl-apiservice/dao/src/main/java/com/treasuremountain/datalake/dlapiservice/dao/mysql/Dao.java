package com.treasuremountain.datalake.dlapiservice.dao.mysql;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gerryzhao on 5/24/2018.
 */
@Component
public class Dao {

    public static  SqlSessionFactory sqlSessionFactory;

    public void init() throws IOException, ClassNotFoundException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }
}
