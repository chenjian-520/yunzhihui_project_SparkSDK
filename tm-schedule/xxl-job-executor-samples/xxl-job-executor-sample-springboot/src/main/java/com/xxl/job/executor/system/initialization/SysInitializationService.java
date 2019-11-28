package com.xxl.job.executor.system.initialization;

import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.xxl.job.executor.system.SysExecExecutorClassEntity;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SysInitializationService {

    @Value("${tm.dlapi.url}")
    private String dlapiUrlConf;

    @Value("${tmexec.http.timeout}")
    private int httptimeout;

    @Value("${tmexec.http.poolingmaxtotal}")
    private int poolingmaxtotal;

    public static List<SysExecExecutorClassEntity> sysExecExecutorCache = new ArrayList<>();

    public static String dlapiUrl;

    public static SqlSessionFactory sqlSessionFactory;

    @PostConstruct
    public void init() throws IOException {
        dlapiUrl = dlapiUrlConf;

        TMHttpClient.init(httptimeout, poolingmaxtotal);
    }

}
