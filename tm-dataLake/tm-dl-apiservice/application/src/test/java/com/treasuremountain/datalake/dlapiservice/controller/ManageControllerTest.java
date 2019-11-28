package com.treasuremountain.datalake.dlapiservice.controller;

import com.treasuremountain.datalake.dlapiservice.ApiServiceApplication;
import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBtableConfigDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/11 16:18
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManageControllerTest {



    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        String url = String.format("http://localhost:%d/", port);
        System.out.println(String.format("port is : [%d]", port));
        this.base = new URL(url);
    }

    @Test
    public void test1() throws Exception {
        HBtableConfigDto hBtableConfigDto =new HBtableConfigDto();
        hBtableConfigDto.setHbtableId("");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<HBtableConfigDto> request = new HttpEntity<>(hBtableConfigDto, headers);
        ResponseEntity<String> response = this.restTemplate.postForEntity(this.base+"hbaseconfig",request,String.class);

        System.out.println(String.format("测试结果为：%s", response.getBody()));
    }

}
