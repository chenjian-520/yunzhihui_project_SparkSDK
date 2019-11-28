package com.xxl.job.admin.core.conf;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @program: computation
 * @description: 邮件配置
 * @author: songchao
 * @creat: 2019-10-29 15:37
 **/
@Configuration
public class XxlJobMailConfig implements InitializingBean {

    @Value("${xxl.job.http.mail.host}")
    private String mailHost;

    @Value("${xxl.job.http.mail.port}")
    private String mailPort;

    @Value("${xxl.job.http.mail.uri}")
    private String mailUri;

    public XxlJobMailConfig() {
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public String getMailPort() {
        return mailPort;
    }

    public void setMailPort(String mailPort) {
        this.mailPort = mailPort;
    }

    public String getMailUri() {
        return mailUri;
    }

    public void setMailUri(String mailUri) {
        this.mailUri = mailUri;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TMHttpClient.init();
        config = this;
    }

    private static XxlJobMailConfig config = null;

    public static XxlJobMailConfig getConfig() {
        return config;
    }
}
