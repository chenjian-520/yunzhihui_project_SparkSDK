package com.treasuremountain.machinecloudreceiver.service.datareceiver;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.common.message.BusinessDataMsg;
import com.treasuremountain.datalake.dlapiservice.common.message.ColumnKv;
import com.treasuremountain.datalake.dlapiservice.common.message.RowKv;
import com.treasuremountain.datalake.dlapiservice.common.message.TMDLType;
import com.treasuremountain.machinecloudreceiver.cache.DatarelationCache;
import com.treasuremountain.machinecollector.common.entity.KepwareValueEntity;
import com.treasuremountain.machinecollector.common.message.KepwareIoTMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLHbOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMDataFlowStep;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMlogMaker;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.tools.MD5Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class DataReceiverService {

    private static Gson gson = new Gson();

    private final static Logger log = LoggerFactory.getLogger(DataReceiverService.class);

    public static void intReceiver(String queryName, String persistenceUrl) throws IOException {
        TMRabbitMqOperator.queryRevive(queryName, (d) -> {
            try {
                String msg = d.getMsg();
                KepwareIoTMsg dataMsg = gson.fromJson(msg, KepwareIoTMsg.class);

//                String logStr1 = TMlogMaker.dataFlow("", "",
//                        msg, TMDataFlowStep.MGkpCloudReceived, "is received", 1);
//                log.info(logStr1);

                List<BusinessDataMsg> businessDataMsgs = converTobd(dataMsg);
                if (sedToDL(businessDataMsgs, persistenceUrl)) {
                    d.basicAck();

//                    String logStr2 = TMlogMaker.dataFlow("", "",
//                            gson.toJson(businessDataMsgs), TMDataFlowStep.MGkpCloudSendToDL, "is sent", 1);
//                    log.info(logStr2);

                } else {
                    d.basicReject(true);
                }
            } catch (Exception ex) {
                log.error(ex.toString());
            }
        });
    }

    private static boolean sedToDL(List<BusinessDataMsg> businessDataMsgs, String persistenceUrl) throws InterruptedException {

        final boolean[] isSuccess = {true};

        CountDownLatch latch1 = new CountDownLatch(businessDataMsgs.size());

        businessDataMsgs.forEach(d -> {
            TMHttpClient.doPost(persistenceUrl + d.getBusinessId(), null, gson.toJson(d), (parm) -> {
                if (parm.getStatusCode() != 200 && parm.getStatusCode() != 201) {
                    isSuccess[0] = false;
                    String errorMsg = parm.getResponseBody();
                    log.error(errorMsg);
                }
                latch1.countDown();
            });
        });

        latch1.await();

        return isSuccess[0];
    }

    private static List<BusinessDataMsg> converTobd(KepwareIoTMsg dataMsg) {
        List<KepwareValueEntity> kepwareValueEntities = dataMsg.getValues();

        List<BusinessDataMsg> businessDataMsgs = new ArrayList<>();

        kepwareValueEntities.forEach(d -> {
            String id = d.getId();
            String v = d.getV();
            long t = d.getT();

            DatarelationCache.DatarelationList.stream().filter(f -> f.getDatarelationKey().equals(id)).findFirst().map(g -> {

                RowKv rowKv = new RowKv();

                String rowkey = TMDLHbOperator.makeCommonRowKey(String.valueOf(System.currentTimeMillis()), 20);

                List<ColumnKv> columnKvList = new ArrayList<>();

                ColumnKv c1 = new ColumnKv();
                c1.setKey("rowkey");
                c1.setValue(rowkey);
                c1.setType(TMDLType.STRING);

                columnKvList.add(c1);

                ColumnKv c2 = new ColumnKv();
                c2.setKey("id");
                c2.setValue(id);
                c2.setType(TMDLType.STRING);

                columnKvList.add(c2);

                ColumnKv c3 = new ColumnKv();
                c3.setKey("v");
                c3.setValue(v);
                c3.setType(TMDLType.STRING);

                columnKvList.add(c3);

                ColumnKv c4 = new ColumnKv();
                c4.setKey("t");
                c4.setValue(Long.toString(t));
                c4.setType(TMDLType.LONG);

                columnKvList.add(c4);

                ColumnKv c5 = new ColumnKv();
                c5.setKey("org");
                c5.setValue(dataMsg.getOrg());
                c5.setType(TMDLType.STRING);

                columnKvList.add(c5);

                ColumnKv c6 = new ColumnKv();
                c6.setKey("reserveValue");
                c6.setValue(dataMsg.getReserveValue());
                c6.setType(TMDLType.STRING);

                columnKvList.add(c6);

                rowKv.setColumnKvList(columnKvList);

                Optional<BusinessDataMsg> obd = businessDataMsgs.stream().filter(h -> h.getBusinessId()
                        .equals(g.getBusinessId())).findFirst();

                if (obd.isPresent()) {
                    obd.get().getRowKvList().add(rowKv);
                } else {

                    List<RowKv> rowKvList = new ArrayList<>();
                    rowKvList.add(rowKv);

                    BusinessDataMsg businessDataMsg = new BusinessDataMsg();
                    businessDataMsg.setBusinessId(g.getBusinessId());
                    businessDataMsg.setRowKvList(rowKvList);

                    businessDataMsgs.add(businessDataMsg);
                }


                return true;
            });
        });

        return businessDataMsgs;
    }
}
