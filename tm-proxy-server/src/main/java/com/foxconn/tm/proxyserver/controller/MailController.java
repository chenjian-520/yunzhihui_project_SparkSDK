package com.foxconn.tm.proxyserver.controller;

import com.foxconn.tm.proxyserver.util.MaxHttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: computation
 * @description: 邮件发送
 * @author: songchao
 * @creat: 2019-11-06 09:48
 **/
@RestController
@Slf4j
public class MailController {

    private final MaxHttpClientUtils maxHttpClientUtils;

    @Autowired
    public MailController(MaxHttpClientUtils maxHttpClientUtils) {
        this.maxHttpClientUtils = maxHttpClientUtils;
    }

    @Value("${xxl.job.http.mail.host}")
    private String mailHost;

    @Value("${xxl.job.http.mail.port}")
    private String mailPort;

    @Value("${xxl.job.http.mail.uri}")
    private String mailUri;

    /**
     *
     * @param to 收件地址
     * @param subject 主体标题
     * @param text 内容
     * @return
     */
    @PostMapping(value = "sendmail")
    public Boolean index(String to, String subject, String text) {
        log.info("发送邮件："+to+":"+subject);
        // 判断非空
        if (StringUtils.isEmpty(to) || StringUtils.isEmpty(subject) || StringUtils.isEmpty(text)) {
            log.error(String.format("发送邮件必要参数不能为空! to:%s,subject:%s,text:%s", to, subject, text));
            return Boolean.FALSE;
        }

        String url = mailHost + ":" + mailPort + mailUri;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpResponse httpResponse = null;
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("to", to));
        nvps.add(new BasicNameValuePair("subject", subject));
        nvps.add(new BasicNameValuePair("text", text));

        //设置参数到请求对象中
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.error(String.format("发送邮件失败! to:%s,subject:%s,text:%s", to, subject, text));
            return Boolean.FALSE;
        }
        try {
            httpResponse = maxHttpClientUtils.execution(httpPost, null);
        } catch (IOException e) {
            log.error(String.format("发送邮件失败! to:%s,subject:%s,text:%s", to, subject, text));
            return Boolean.FALSE;

        }
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = httpResponse.getEntity();
            try {
                String result = EntityUtils.toString(entity);
                return Boolean.parseBoolean(result);
            } catch (IOException e) {
                log.error(String.format("发送邮件失败! to:%s,subject:%s,text:%s", to, subject, text));
                return Boolean.FALSE;

            }
        } else {
            HttpEntity entity = httpResponse.getEntity();
            String result = null;
            try {
                result = EntityUtils.toString(entity);
            } catch (IOException e) {
                log.error("发送邮件失败: 得到返回信息异常");
            }
            log.error("发送邮件失败: " + result);
        }
        return Boolean.FALSE;
    }
}
