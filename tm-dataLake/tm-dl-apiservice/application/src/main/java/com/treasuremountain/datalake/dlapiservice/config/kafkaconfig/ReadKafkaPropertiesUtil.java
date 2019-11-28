package com.treasuremountain.datalake.dlapiservice.config.kafkaconfig;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * created by Axin on 2019/6/26
 */
@Component
@Slf4j
public class ReadKafkaPropertiesUtil {

    /**
     * 属性
     */
    private static Properties properties;

    /**
     * 读取kafka.properties
     */
    static {
        // kafkaconfig.properties路径
        log.debug("{}"," read kafkaconfig.properties ");

        properties = new Properties();

        Resource resource = new ClassPathResource("kafka.properties");
        try {
            properties.load(resource.getInputStream());
        } catch (IOException e) {
            log.error("{}"," Kafka Produce init kafkaconfig properties " + e);
        }

    }

    /**
     * 获取kafka的配置信息
     *
     * @return
     */
    public static Properties getProperties() {
        return properties;
    }

    /**
     * 私有构造函数
     */
    private ReadKafkaPropertiesUtil() { }
}
