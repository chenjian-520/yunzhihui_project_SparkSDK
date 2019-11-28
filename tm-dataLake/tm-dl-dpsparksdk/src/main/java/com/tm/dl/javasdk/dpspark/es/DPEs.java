package com.tm.dl.javasdk.dpspark.es;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.PermissionManager;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSourceBatchMsg;
import org.apache.http.nio.reactor.IOReactorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DPEs {
    public static void init() throws IOReactorException {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        pm.initialEsSecurityContext();
    }

    public static void addIndexBatch(String tableName, List<MTSourceBatchMsg> mtSourceBatchMsgList) throws InterruptedException {
        CountDownLatch latch1 = new CountDownLatch(1);
        MTElasticsearchOperator.addsourceBatch(tableName, "hb", mtSourceBatchMsgList, d -> {
            if (d.getStatusCode() != 200 && d.getStatusCode() != 201) {
                System.out.println(d.getResponseBody());
            }
            latch1.countDown();
        });
        latch1.await();
    }

    public static List queryDsl(String indexName, String dsl) throws Exception {
        return MTElasticsearchOperator.queryDSL(new ArrayList<String>() {{
            add(indexName);
        }}, dsl).getRows();
    }

    public static List querySql(String sql) throws Exception {
        return MTElasticsearchOperator.querySql(sql).getRows();
    }


}
