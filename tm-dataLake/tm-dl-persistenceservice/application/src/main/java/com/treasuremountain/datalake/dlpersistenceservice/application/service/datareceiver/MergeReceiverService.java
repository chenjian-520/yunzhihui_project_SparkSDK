package com.treasuremountain.datalake.dlpersistenceservice.application.service.datareceiver;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.common.message.BusinessDataMsg;
import com.treasuremountain.datalake.dlapiservice.common.message.RowKv;
import com.treasuremountain.datalake.dlpersistenceservice.application.entity.MergeData;
import com.treasuremountain.datalake.dlpersistenceservice.application.entity.MergeSendData;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.message.QueryCallbackMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MergeReceiverService {
    private static Gson gson = new Gson();

    private final static Logger log = LoggerFactory.getLogger(MergeReceiverService.class);

    public static Timer timer = new Timer(true);

    private static ConcurrentLinkedQueue linkedQueue = new ConcurrentLinkedQueue();

    private static Map<String, MergeSendData> msgMap = new HashMap<>();

    private static boolean isRunning = false;

    public static void intReceiver(String queryName) throws IOException {
        TMRabbitMqOperator.queryRevive(queryName, (d) -> {

//            System.out.print("########\n");

            BusinessDataMsg dataMsg = gson.fromJson(d.getMsg(), BusinessDataMsg.class);
            List<RowKv> rowKvs = dataMsg.getRowKvList();

            if (rowKvs != null && rowKvs.size() > 0
                    && rowKvs.get(0).getColumnKvList() != null
                    && rowKvs.get(0).getColumnKvList().size() > 0) {

                MergeData mergeData = new MergeData();
                mergeData.setBusinessDataMsg(dataMsg);
                mergeData.setCallbackMsg(d);

                linkedQueue.offer(mergeData);
            }

        });
    }

    private static void sendMQ(String mergeQuery, String msg, List<QueryCallbackMsg> sdqcm, String businessId, Iterator iter) {
        try {
            TMRabbitMqOperator.publishQuery(mergeQuery, msg);
            for (int i = 0; i < sdqcm.size(); i++) {
//                sdqcm.get(i).basicReject(true);
                sdqcm.get(i).basicAck();
            }
            if (iter != null) {
                iter.remove();
            }
            msgMap.remove(businessId);
//            System.out.println("sned and remove businessId");
        } catch (IOException ex) {
            log.error(ex.toString());
        }
    }

    private static void mergeMsg(int sendCount, String mergeQuery, long rate) {
        while (!linkedQueue.isEmpty()) {
            preparation(sendCount, mergeQuery, rate);
        }
        sentTimeoutMsg(rate, mergeQuery);
    }

    private static void sentTimeoutMsg(long rate, String mergeQuery) {
        try {

            Iterator iter = msgMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();

                MergeSendData mergeSendData = msgMap.get(key);

                BusinessDataMsg sdbusinessDataMsg = mergeSendData.getBusinessDataMsg();
                long sdtime = mergeSendData.getTime();

                long currentTime = new Date().getTime();

                if ((currentTime - sdtime) >= rate) {
                    String msg = gson.toJson(sdbusinessDataMsg);
                    List<QueryCallbackMsg> sdqcm = mergeSendData.getQueryCallbackMsgs();
                    sendMQ(mergeQuery, msg, sdqcm, key, iter);
                }

                mergeSendData.setTime(currentTime);
            }

        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    private static void preparation(int sendCount, String mergeQuery, long rate) {
        MergeData mergeData = (MergeData) linkedQueue.poll();
        QueryCallbackMsg queryCallbackMsg = mergeData.getCallbackMsg();
        BusinessDataMsg businessDataMsg = mergeData.getBusinessDataMsg();
        String businessId = businessDataMsg.getBusinessId();
        List<RowKv> mergeDataRowKvs = businessDataMsg.getRowKvList();

        MergeSendData mergeSendData = msgMap.get(businessId);
        if (mergeSendData != null) {
            BusinessDataMsg sdbusinessDataMsg = mergeSendData.getBusinessDataMsg();
            List<RowKv> sdsendrowlist = sdbusinessDataMsg.getRowKvList();
            sdsendrowlist.addAll(mergeDataRowKvs);
            mergeSendData.getQueryCallbackMsgs().add(queryCallbackMsg);

            long currentTime = new Date().getTime();

            if (sdsendrowlist.size() >= sendCount) {
                String msg = gson.toJson(sdbusinessDataMsg);
                List<QueryCallbackMsg> sdqcm = mergeSendData.getQueryCallbackMsgs();
                sendMQ(mergeQuery, msg, sdqcm, businessId, null);
            }

            mergeSendData.setTime(currentTime);
        } else {
            List<QueryCallbackMsg> mqcb = new ArrayList<>();
            mqcb.add(queryCallbackMsg);

            MergeSendData mesd = new MergeSendData();
            mesd.setBusinessDataMsg(businessDataMsg);
            mesd.setQueryCallbackMsgs(mqcb);
            mesd.setTime(new Date().getTime());

            msgMap.put(businessId, mesd);

            if (mergeDataRowKvs.size() >= sendCount) {
                String msg = gson.toJson(businessDataMsg);
                sendMQ(mergeQuery, msg, mqcb, businessId, null);
            }
        }
    }

    public static void autoSentMsg(int sendCount, String mergeQuery, long rate) {
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    if (!isRunning) {
                        isRunning = true;
                        mergeMsg(sendCount, mergeQuery, rate);
                        isRunning = false;
                    }
                } catch (Exception ex) {
                    isRunning = false;
                    log.error(ex.toString());
                }
            }
        };
        timer.schedule(task, 0, rate);
    }
}

