package com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch;

import com.google.gson.Gson;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSearchContentMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSearchDSLMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSourceBatchMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.message.MTHttpCallbackParms;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.nio.reactor.IOReactorException;

import javax.xml.soap.SAAJResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Created by gerryzhao on 10/28/2018.
 */
public class MTElasticsearchOperator {

    private static Gson gson = new Gson();

    private static String esUri;

    public static void init(String uri) throws IOReactorException {
        esUri = uri;
        TMHttpClient.init();
    }

    public static void createIndex(String indexName, String typeName) throws Exception {

        final String[] errorMsg = {""};
        String indexUrl = choiceURI() + "/" + indexName;
        String indexbody = "{\"settings\": {\"index.translog.durability\" : \"async\",\"index.translog.sync_interval\": \"300s\",\"index.translog.flush_threshold_size\": \"1024mb\",\"number_of_replicas\" : 0,\"index.merge.scheduler.max_thread_count\": 1, \"index.codec\":\"best_compression\"}}";

        CountDownLatch latch1 = new CountDownLatch(1);

        TMHttpClient.doPut(indexUrl, null, indexbody, (parm) -> {
            if (parm.getStatusCode() != 200 && parm.getStatusCode() != 201) {
                errorMsg[0] = parm.getResponseBody();
            }
            latch1.countDown();
        });

        latch1.await();

        CountDownLatch latch2 = new CountDownLatch(1);

        String mapUrl = String.format(choiceURI() + "/%s/%s/_mapping", indexName, typeName);
        String mapbody = "{ \"properties\": { \"content\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\", \"search_analyzer\": \"ik_max_word\" } },\"_all\": {\"enabled\": false},\"_source\": {\"includes\":[\"content\"]} }";

        TMHttpClient.doPost(mapUrl, null, mapbody, (parm) -> {
            if (parm.getStatusCode() != 200 && parm.getStatusCode() != 201) {
                errorMsg[0] = parm.getResponseBody();
            }
            latch2.countDown();
        });

        latch2.await();

        if (StringUtils.isNotBlank(errorMsg[0])) {
            throw new Exception(errorMsg[0]);
        }
    }

    public static String deleteIndex(String indexName) throws Exception {
        String url = String.format("%s/%s", choiceURI(), indexName);
        return TMHttpClient.doAsyncDelete(url, null);
    }

    public static void addsource(String indexName, String typeName,
                                 String id, String body, Consumer<MTHttpCallbackParms> callback) {

        String sourceUrl = String.format(choiceURI() + "/%s/%s/%s", indexName, typeName, id);

        TMHttpClient.doPost(sourceUrl, null, body, (parm) -> {
            callback.accept(parm);
        });
    }

    public static void addsourceBatch(String indexName, String typeName,
                                      List<MTSourceBatchMsg> mtSourceBatchMsgs, Consumer<MTHttpCallbackParms> callback) {
        String sourceUrl = String.format(choiceURI() + "/%s/%s/_bulk", indexName, typeName);
        StringBuilder hsb = new StringBuilder();

        mtSourceBatchMsgs.forEach(d -> {
            String id = d.getId();

            JSONObject actionBody = new JSONObject();
            actionBody.put("_id", id);

            JSONObject actionObject = new JSONObject();
            actionObject.put("index", actionBody.toString());

            String action = String.format("%s\n%s\n", actionObject.toString(), d.getBody().toString());
            hsb.append(action);
        });

        TMHttpClient.doPost(sourceUrl, null, hsb.toString(), (parm) -> {
            callback.accept(parm);
        });
    }

    public static MTSearchContentMsg query(String index, String where) throws Exception {

        final String[] errorMsg = {""};
        final String[] requestStr = {""};

        String sql = String.format("SELECT content FROM %s WHERE %s", index, where);

        String queryUrl = choiceURI() + "/_xpack/sql";
        String body = String.format("{\"query\": \"%s\"}", sql);

        CountDownLatch latch1 = new CountDownLatch(1);

        TMHttpClient.doPost(queryUrl, null, body, (parm) -> {
            if (parm.getStatusCode() != 200 && parm.getStatusCode() != 201) {
                errorMsg[0] = parm.getResponseBody();
            } else {
                requestStr[0] = parm.getResponseBody();
            }
            latch1.countDown();
        });

        latch1.await();

        if (StringUtils.isNotBlank(errorMsg[0])) {
            throw new Exception(errorMsg[0]);
        }

        return gson.fromJson(requestStr[0], MTSearchContentMsg.class);
    }

    public static MTSearchContentMsg queryDSL(List<String> indexs, String dsl) throws Exception {

        StringBuilder indexStr = new StringBuilder();

        int inedxSize = indexs.size();
        for (int i = 0; i < inedxSize; i++) {
            indexStr.append(indexs.get(i));
            if (i + 1 < inedxSize) {
                indexStr.append(",");
            }
        }
        String url = String.format("%s/%s/_search", choiceURI(), indexStr.toString());
        String result = TMHttpClient.doAsyncPost(url, null, dsl);

        MTSearchContentMsg contentMsg = new MTSearchContentMsg();
        List<List<String>> rows = new ArrayList<>();

        MTSearchDSLMsg robj = gson.fromJson(result, MTSearchDSLMsg.class);
        robj.getHits().getHits().forEach(d -> {
            String content = d.get_source().getContent();

            List<String> row = new ArrayList<>();
            row.add(content);

            rows.add(row);
        });

        contentMsg.setRows(rows);

        return contentMsg;
    }

    public static MTSearchContentMsg querySql(String sql) throws Exception {
        String url = choiceURI() + "/_xpack/sql?format=json";
        String body = String.format("{\"query\": \"%s\"}", sql);
        String result = TMHttpClient.doAsyncPost(url, null, body);
        MTSearchContentMsg contentMsg = new MTSearchContentMsg();
        List<List<String>> rows = new ArrayList<>();
        MTSearchDSLMsg robj = gson.fromJson(result, MTSearchDSLMsg.class);
        robj.getHits().getHits().forEach(d -> {
            String content = d.get_source().getContent();

            List<String> row = new ArrayList<>();
            row.add(content);

            rows.add(row);
        });

        contentMsg.setRows(rows);
        return contentMsg;
    }

    public static String bulkQuery(String indexName, String typeName, String dsl) throws Exception {
        String url = null;
        if (typeName == null) {
            url = String.format("%s/%s/_mget", choiceURI(), indexName);
        } else {
            url = String.format("%s/%s/%s/_mget", choiceURI(), indexName, typeName);
        }
        return TMHttpClient.doAsyncPost(url, null, dsl);
    }

    public static String translate(String sql) throws Exception {
        String url = choiceURI() + "/_xpack/sql/translate";
        String body = String.format("{\"query\": \"%s\"}", sql);

        return TMHttpClient.doAsyncPost(url, null, body);
    }

    private static String choiceURI() {
        List<String> uriList = Arrays.asList(esUri.split(","));

        int uc = uriList.size();

        Random ra = new Random();
        int ci = ra.nextInt(uc);

        return uriList.get(ci);
    }

}
