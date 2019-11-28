package com.treasuremountain.datalake.dlapiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by gerryzhao on 10/16/2018.
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ServletComponentScan
public class ApiServiceApplication {
    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(ApiServiceApplication.class, args);
    }
}
