package com.treasuremountain.datalake.dlpersistenceservice.application.service.datareceiver;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlpersistenceservice.application.entity.IndexMsg;
import com.treasuremountain.datalake.dlpersistenceservice.application.entity.IndexValue;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSourceBatchMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class IndexReceiverService {
    private static Gson gson = new Gson();

    private final static Logger log = LoggerFactory.getLogger(IndexReceiverService.class);

    public static void intReceiver(String indexQuery) throws IOException {

        TMRabbitMqOperator.queryRevive(indexQuery, (d) -> {

            final boolean[] isSuccess = {true};

            IndexMsg msg = gson.fromJson(d.getMsg(), IndexMsg.class);

            List<MTSourceBatchMsg> sourceBatchMsgList = new ArrayList<>();

            msg.getIndexBodyList().stream().forEach(e -> {
                String id = e.getId();
                List<IndexValue> valueList = e.getBody();

                JSONObject indexBody = new JSONObject();

                MTSourceBatchMsg mtSourceBatchMsg = new MTSourceBatchMsg();
                mtSourceBatchMsg.setId(id);

                valueList.stream().forEach(f -> {

                    String indexKey = f.getKey();
                    String value = f.getValue();

                    try {
                        switch (f.getType()) {
                            case INT:
                                indexBody.put(indexKey, Integer.parseInt(value));
                                break;
                            case LONG:
                                indexBody.put(indexKey, Long.valueOf(value));
                                break;
                            case DOUBLE:
                                indexBody.put(indexKey, Double.valueOf(value));
                                break;
                            default:
                                indexBody.put(indexKey, value);
                                break;
                        }
                    } catch (Exception ex) {
                        log.error(ex.toString());
                    }

                });

                mtSourceBatchMsg.setBody(indexBody);
                sourceBatchMsgList.add(mtSourceBatchMsg);
            });

            try {
                CountDownLatch latch1 = new CountDownLatch(1);

                MTElasticsearchOperator.addsourceBatch(msg.getIndexName(), "hb", sourceBatchMsgList, e -> {
                    if (e.getStatusCode() != 200 && e.getStatusCode() != 201) {
                        isSuccess[0] = false;
                    }
                    latch1.countDown();
                });

                latch1.await();
            } catch (Exception ex) {
                isSuccess[0] = false;
            }

            try {
                if (isSuccess[0]) {
                    d.basicAck();
                } else {
                    d.basicReject(true);
                }
            } catch (Exception ex) {
                log.error(ex.toString());
            }

        });

    }
}
