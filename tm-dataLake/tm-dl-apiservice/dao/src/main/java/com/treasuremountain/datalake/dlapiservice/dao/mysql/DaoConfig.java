package com.treasuremountain.datalake.dlapiservice.dao.mysql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/6/21.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Configuration
@Slf4j
@Order(1)
public class DaoConfig {

    @Bean
    public Dao dao() {
        try {
            Dao dao = new Dao();
            dao.init();
            return dao;
        } catch (Exception e) {
            log.error("初始化数据库异常：", e);
            return null;
        }
    }
}
