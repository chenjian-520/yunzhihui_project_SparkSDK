package com.treasuremountain.tmcommon.thirdpartyservice.httpclient;

import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.message.MTHttpCallbackParms;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Created by gerryzhao on 10/28/2018.
 */
public class TMHttpClient {

    private static CloseableHttpAsyncClient httpclient = null;

    public static void init() throws IOReactorException {
        if (httpclient == null) {
            startClient(0, 0);
        }
    }

    public static void init(int timeout, int maxTotal) throws IOReactorException {
        if (httpclient == null) {
            startClient(maxTotal, timeout);
        }
    }

    private static void startClient(int maxTotal, int timeout) throws IOReactorException {
        HttpAsyncClientBuilder builder = HttpAsyncClients.custom();
        if (maxTotal != 0) {
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
            PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
            cm.setMaxTotal(100);
            builder.setConnectionManager(cm);
        }
        if (timeout != 0) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .build();
            builder.setDefaultRequestConfig(requestConfig);
        }
        httpclient = builder.build();
        httpclient.start();
    }

    public static void doPut(String uri, HashMap<String, String> header, String body,
                             final Consumer<MTHttpCallbackParms> callback) {
        HttpPut request = new HttpPut(uri);

        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                request.addHeader(key, header.get(key));
            }
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(getRequestBody(body));
        }

        httpclient.execute(request, new FutureCallback<HttpResponse>() {

            @Override
            public void completed(final HttpResponse response) {
                callback.accept(getCallbackPar(response));
            }

            @Override
            public void failed(final Exception ex) {
                callback.accept(getFailedPar(ex));
            }

            @Override
            public void cancelled() {
                callback.accept(getCancelledPar());
            }
        });

    }

    public static String doAsyncPut(String uri, HashMap<String, String> header, String body) throws Exception {
        final String[] requestStr = {""};
        final String[] errorMsg = {""};

        CountDownLatch latch1 = new CountDownLatch(1);

        TMHttpClient.doPut(uri, header, body, (parm) -> {
            String resbody = parm.getResponseBody();
            if (parm.getStatusCode() == 200 || parm.getStatusCode() == 201) {
                requestStr[0] = resbody;
            } else {
                errorMsg[0] = resbody;
            }
            latch1.countDown();
        });

        latch1.await();

        if (StringUtils.isNotBlank(errorMsg[0])) {
            throw new Exception(errorMsg[0]);
        }

        return requestStr[0];
    }


    public static void doPost(String uri, HashMap<String, String> header, String body,
                              final Consumer<MTHttpCallbackParms> callback) {
        HttpPost request = new HttpPost(uri);

        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                request.addHeader(key, header.get(key));
            }
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(getRequestBody(body));
        }

        httpclient.execute(request, new FutureCallback<HttpResponse>() {

            @Override
            public void completed(final HttpResponse response) {
                callback.accept(getCallbackPar(response));
            }

            @Override
            public void failed(final Exception ex) {
                callback.accept(getFailedPar(ex));
            }

            @Override
            public void cancelled() {
                callback.accept(getCancelledPar());
            }
        });

    }

    public static String doAsyncPost(String uri, HashMap<String, String> header, String body) throws Exception {
        final String[] requestStr = {""};
        final String[] errorMsg = {""};

        CountDownLatch latch1 = new CountDownLatch(1);

        TMHttpClient.doPost(uri, header, body, (parm) -> {
            String resbody = parm.getResponseBody();
            if (parm.getStatusCode() == 200 || parm.getStatusCode() == 201) {
                requestStr[0] = resbody;
            } else {
                errorMsg[0] = resbody;
            }
            latch1.countDown();
        });

        latch1.await();

        if (StringUtils.isNotBlank(errorMsg[0])) {
            throw new Exception(errorMsg[0]);
        }

        return requestStr[0];
    }

    public static void doDelete(String uri, HashMap<String, String> header,
                                final Consumer<MTHttpCallbackParms> callback) {
        final HttpDelete request = new HttpDelete(uri);

        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                request.addHeader(key, header.get(key));
            }
        }

        httpclient.execute(request, new FutureCallback<HttpResponse>() {

            @Override
            public void completed(final HttpResponse response) {
                callback.accept(getCallbackPar(response));
            }

            @Override
            public void failed(final Exception ex) {
                callback.accept(getFailedPar(ex));
            }

            @Override
            public void cancelled() {
                callback.accept(getCancelledPar());
            }

        });
    }

    public static String doAsyncDelete(String uri, HashMap<String, String> header) throws Exception {
        final String[] requestStr = {""};
        final String[] errorMsg = {""};

        CountDownLatch latch1 = new CountDownLatch(1);

        TMHttpClient.doDelete(uri, header, (parm) -> {
            String resbody = parm.getResponseBody();
            if (parm.getStatusCode() == 200 || parm.getStatusCode() == 201) {
                requestStr[0] = resbody;
            } else {
                errorMsg[0] = resbody;
            }
            latch1.countDown();
        });

        latch1.await();

        if (StringUtils.isNotBlank(errorMsg[0])) {
            throw new Exception(errorMsg[0]);
        }

        return requestStr[0];
    }

    public static void doGet(String uri, HashMap<String, String> header,
                             final Consumer<MTHttpCallbackParms> callback) {

        final HttpGet request = new HttpGet(uri);

        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                request.addHeader(key, header.get(key));
            }
        }

        httpclient.execute(request, new FutureCallback<HttpResponse>() {

            @Override
            public void completed(final HttpResponse response) {
                callback.accept(getCallbackPar(response));
            }

            @Override
            public void failed(final Exception ex) {
                callback.accept(getFailedPar(ex));
            }

            @Override
            public void cancelled() {
                callback.accept(getCancelledPar());
            }

        });
    }

    public static String doAsyncGet(String uri, HashMap<String, String> header) throws Exception {

        final String[] requestStr = {""};
        final String[] errorMsg = {""};

        CountDownLatch latch1 = new CountDownLatch(1);

        TMHttpClient.doGet(uri, header, (parm) -> {
            String resbody = parm.getResponseBody();
            if (parm.getStatusCode() == 200 || parm.getStatusCode() == 201) {
                requestStr[0] = resbody;
            } else {
                errorMsg[0] = resbody;
            }
            latch1.countDown();
        });

        latch1.await();

        if (StringUtils.isNotBlank(errorMsg[0])) {
            throw new Exception(errorMsg[0]);
        }

        return requestStr[0];
    }

    public static void close() throws IOException {
        httpclient.close();
    }

    private static StringEntity getRequestBody(String body) {
        StringEntity entity = new StringEntity(body, Charset.forName("UTF-8"));
        entity.setContentType("application/json");
        entity.setContentEncoding("UTF-8");
        return entity;
    }

    private static MTHttpCallbackParms getCancelledPar() {
        MTHttpCallbackParms mtHttpCallbackParms = new MTHttpCallbackParms();
        mtHttpCallbackParms.setStatusCode(0);
        mtHttpCallbackParms.setResponseBody("cancelled");
        return mtHttpCallbackParms;
    }

    private static MTHttpCallbackParms getFailedPar(Exception ex) {
        MTHttpCallbackParms mtHttpCallbackParms = new MTHttpCallbackParms();
        mtHttpCallbackParms.setStatusCode(500);
        mtHttpCallbackParms.setResponseBody(ex.toString());
        return mtHttpCallbackParms;
    }

    private static MTHttpCallbackParms getCallbackPar(HttpResponse response) {
        MTHttpCallbackParms mtHttpCallbackParms = new MTHttpCallbackParms();

        int statusCode = 0;
        String body = getResponseBody(response);
        statusCode = response.getStatusLine().getStatusCode();

        mtHttpCallbackParms.setStatusCode(statusCode);
        mtHttpCallbackParms.setResponseBody(body);

        return mtHttpCallbackParms;
    }

    private static String getResponseBody(HttpResponse response) {
        String body = "";
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                final InputStream instream = entity.getContent();
                try {
                    final StringBuilder sb = new StringBuilder();
                    final char[] tmp = new char[1024];
                    final Reader reader = new InputStreamReader(instream, "utf-8");
                    int l;
                    while ((l = reader.read(tmp)) != -1) {
                        sb.append(tmp, 0, l);
                    }
                    body = sb.toString();
                } finally {
                    instream.close();
                    EntityUtils.consume(entity);
                }
            }
        } catch (Exception ex) {
            body = "";
        }
        return body;
    }

}
