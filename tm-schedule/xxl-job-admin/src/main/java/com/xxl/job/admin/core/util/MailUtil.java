package com.xxl.job.admin.core.util;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.conf.XxlJobMailConfig;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮件发送.Util
 *
 * @author xuxueli 2016-3-12 15:06:20
 */
@Component
public class MailUtil {
	private static Logger logger = LoggerFactory.getLogger(MailUtil.class);

	private static MaxHttpClientUtils maxHttpClientUtils;

	@Autowired
	public void setMaxHttpClientUtils(MaxHttpClientUtils maxHttpClientUtils) {
		this.maxHttpClientUtils = maxHttpClientUtils;
	}

	/**
	 *
	 * @param toAddress		收件人邮箱
	 * @param mailSubject	邮件主题
	 * @param mailBody		邮件正文
	 * @return
	 */
	public static boolean sendMail(String toAddress, String mailSubject, String mailBody){

		try {
			// Create the email message
			HtmlEmail email = new HtmlEmail();

			//email.setDebug(true);		// 将会打印一些log
			//email.setTLS(true);		// 是否TLS校验，，某些邮箱需要TLS安全校验，同理有SSL校验
			//email.setSSL(true);

			email.setHostName(XxlJobAdminConfig.getAdminConfig().getMailHost());

			if (XxlJobAdminConfig.getAdminConfig().isMailSSL()) {
				email.setSslSmtpPort(XxlJobAdminConfig.getAdminConfig().getMailPort());
				email.setSSLOnConnect(true);
			} else {
				email.setSmtpPort(Integer.valueOf(XxlJobAdminConfig.getAdminConfig().getMailPort()));
			}

			email.setAuthenticator(new DefaultAuthenticator(XxlJobAdminConfig.getAdminConfig().getMailUsername(), XxlJobAdminConfig.getAdminConfig().getMailPassword()));
			email.setCharset("UTF-8");

			email.setFrom(XxlJobAdminConfig.getAdminConfig().getMailUsername(), XxlJobAdminConfig.getAdminConfig().getMailSendNick());
			email.addTo(toAddress);
			email.setSubject(mailSubject);
			email.setMsg(mailBody);

			//email.attach(attachment);	// add the attachment

			email.send();				// send the email
			return true;
		} catch (EmailException e) {
			logger.error(e.getMessage(), e);

		}
		return false;

	}

	/**
	 * 用http接口方法调用接口
	 * @param toAddress		收件人邮箱
	 * @param mailSubject	邮件主题
	 * @param mailBody		邮件正文
	 * @return
	 */
	public static boolean sendMailByHttpRestful(String toAddress, String mailSubject, String mailBody){

		String url = XxlJobMailConfig.getConfig().getMailHost() + ":" + XxlJobMailConfig.getConfig().getMailPort() + XxlJobMailConfig.getConfig().getMailUri();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		HttpResponse httpResponse = null;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("to", toAddress));
		nvps.add(new BasicNameValuePair("subject", mailSubject));
		nvps.add(new BasicNameValuePair("text", mailBody));

		//设置参数到请求对象中
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("发送邮件失败 toAddress:%s,mailSubject:%s,mailBody:%s", toAddress, mailSubject, mailBody));
			return false;
		}
		try {
			httpResponse = maxHttpClientUtils.execution(httpPost, null);
		} catch (IOException e) {
			logger.error(String.format("发送邮件失败 toAddress:%s,mailSubject:%s,mailBody:%s", toAddress, mailSubject, mailBody));
			return false;
		}
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = httpResponse.getEntity();
			try {
				String result = EntityUtils.toString(entity);
				return Boolean.parseBoolean(result);
			} catch (IOException e) {
				logger.error(String.format("发送邮件失败 toAddress:%s,mailSubject:%s,mailBody:%s", toAddress, mailSubject, mailBody));
				return false;
			}
		} else {
			HttpEntity entity = httpResponse.getEntity();
			String result = null;
			try {
				result = EntityUtils.toString(entity);
			} catch (IOException e) {
				logger.error("发送邮件失败: 得到返回信息异常");
			}
			logger.error("发送邮件失败: " + result);
		}
		return false;
	}

}
