package com.treasuremountain.datalake.dlapiservice.service.initialization;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

/**
 * Description: 同步的http client
 * <p>
 * Created by ref.tian on 2018/9/3.
 * Company: Maxnerva
 * Project: MaxIoT
 */
@Slf4j
@Data
public class MaxHttpClientUtils {

    private int connectTimeout;//建立连接超时时间
    //    @Value("${httpclient.socketTimeout}")
    private int socketTimeout;//数据传输超时时间
    //    @Value("${httpclient.connectionRequestTimeout}")
    private int connectionRequestTimeout;//请求超时时间
    //    @Value("${httpclient.defaultMaxPerRoute}")
    private int defaultMaxPerRoute;//单路由的最大并发连接数
    //    @Value("${httpclient.maxTotal}")
    private int maxTotal;//连接池最大连接并发数

    private CloseableHttpClient httpclient;

    /**
     * @param connectTimeout           建立连接超时时间
     * @param socketTimeout            数据传输超时时间
     * @param connectionRequestTimeout 请求超时时间
     * @param defaultMaxPerRoute       单路由的最大并发连接数
     * @param maxTotal                 连接池最大连接并发数
     ***/
    public void init(int connectTimeout, int socketTimeout, int connectionRequestTimeout, int defaultMaxPerRoute, int maxTotal) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        this.maxTotal = maxTotal;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)//设置为20ms
                .build();

        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();

        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);

        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(defaultMaxPerRoute);

        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        httpclient= HttpClients.custom().setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig).
                        setRetryHandler(httpRequestRetryHandler)
                .build();
    }

    public CloseableHttpResponse execution(HttpRequestBase httpRequestBase, Consumer<Boolean> result) throws IOException {
        return httpclient.execute(httpRequestBase);
    }
}
