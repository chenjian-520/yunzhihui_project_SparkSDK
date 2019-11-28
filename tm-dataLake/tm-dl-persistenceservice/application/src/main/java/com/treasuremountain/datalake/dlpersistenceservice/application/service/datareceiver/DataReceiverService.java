package com.treasuremountain.datalake.dlpersistenceservice.application.service.datareceiver;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.HBTableEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.RelationEntity;
import com.treasuremountain.datalake.dlapiservice.common.message.BusinessDataMsg;
import com.treasuremountain.datalake.dlapiservice.common.message.ColumnKv;
import com.treasuremountain.datalake.dlapiservice.common.message.RowKv;
import com.treasuremountain.datalake.dlapiservice.common.message.TMDLType;
import com.treasuremountain.datalake.dlpersistenceservice.application.entity.IndexBody;
import com.treasuremountain.datalake.dlpersistenceservice.application.entity.IndexMsg;
import com.treasuremountain.datalake.dlpersistenceservice.application.entity.IndexValue;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSourceBatchMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLHbOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseColumnEntity;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseColumnFamilyEntity;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseRowEntity;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.message.MTHttpCallbackParms;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMDataFlowStep;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMlogMaker;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.tools.StringTools;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by gerryzhao on 10/21/2018.
 */
@Service
public class DataReceiverService {

    private static Gson gson = new Gson();

    private final static Logger log = LoggerFactory.getLogger(DataReceiverService.class);

    public static void intReceiver(String dataQuery, String indexQuery) throws IOException {

        TMRabbitMqOperator.queryRevive(dataQuery, (d) -> {

            boolean isSuccess = false;
            String errorMsg = "";
            String businessId = "";

            BusinessDataMsg dataMsg = gson.fromJson(d.getMsg(), BusinessDataMsg.class);
            List<RowKv> rowKvs = dataMsg.getRowKvList();

            if (rowKvs != null && rowKvs.size() > 0
                    && rowKvs.get(0).getColumnKvList() != null
                    && rowKvs.get(0).getColumnKvList().size() > 0) {

                businessId = dataMsg.getBusinessId();

//                String logStr = TMlogMaker.dataFlow(businessId, "", d.getMsg(), TMDataFlowStep.DLPersistenceReceived,
//                        "Receive the message", 1);
//                log.info(logStr);

                try {
                    persistenceHbase(businessId, indexQuery, dataMsg);
                    isSuccess = true;
                } catch (Exception ex) {
                    isSuccess = false;
                    errorMsg = ex.toString();
                }


            } else {
                isSuccess = true;
            }

            try {
                if (isSuccess) {
                    d.basicAck();

//                    String logStr = TMlogMaker.dataFlow(businessId, "", d.getMsg(), TMDataFlowStep.DLPersistenceComplate,
//                            "persistence successed", 1);
//                    log.info(logStr);

                } else {
                    d.basicReject(true);

//                    String logStr = TMlogMaker.dataFlow(businessId, "", d.getMsg(), TMDataFlowStep.DLPersistenceComplate,
//                            errorMsg, 0);
//                    log.info(logStr);
                }
            } catch (Exception ex) {
                log.error(ex.toString());
            }
        });
    }

    private static boolean isIndex(HBTableEntity hbTableEntity, String columnFamily, String columnName) {

        final boolean[] isIndex = {false};

        hbTableEntity.getColumnfamilyList().stream().filter(d -> d.getHbcolumnfamilyName().equals(columnFamily)).findFirst().map(e -> {
            e.getColumnList().stream().filter(f -> f.getHbcolumnName().equals(columnName)).findFirst().map(g -> {
                isIndex[0] = g.getHbcolumnIsindex();
                return true;
            });
            return true;
        });
        return isIndex[0];

    }

//    private static void addIndexSource(String id, String tableName, String body, Consumer<MTHttpCallbackParms> callback) {
//        tableName = tableName.replace(":", "_");
//        MTElasticsearchOperator.addsource(tableName, "hb", id, body, callback);
//    }

    private static void persistenceHbase(String businessId, String indexQuery, BusinessDataMsg dataMsg) throws Exception {

        final boolean[] isSuccess = {true};
        final String[] errorMsg = {""};

        BusinessCache.businesList.stream().filter(e -> e.getBusinessId().equals(businessId)).findFirst().map(f -> {

            final boolean[] isPersistence = {true};

            final String[] tableName = {""};

            final boolean[] isTowlevelIndex = {false};

            List<HbaseRowEntity> hbRowList = new ArrayList<>();
            List<IndexBody> indexBodyList = new ArrayList<>();

            List<RelationEntity> relationList = f.getRelationList();
            List<RowKv> rowKvs = dataMsg.getRowKvList();

            rowKvs.forEach(g -> {

                final boolean[] isRowkey = {false};

                isPersistence[0] = true;

                final String[] id = {""};

                IndexBody indexBody = new IndexBody();
                List<IndexValue> indexValueList = new ArrayList<>();

                HbaseRowEntity hbaseRowEntity = new HbaseRowEntity();
                List<HbaseColumnFamilyEntity> hbaseColumnFamilyList = new ArrayList<>();

                List<ColumnKv> columnKvs = g.getColumnKvList();
                columnKvs.forEach(h -> {

                    String key = h.getKey();
                    String value = h.getValue();
                    TMDLType valtype = h.getType();

                    if (StringUtils.isNotBlank(key) && valtype != null) {

                        if (key.equals("rowkey")) {
                            hbaseRowEntity.setRowKey(value);

                            IndexValue contentV = new IndexValue();
                            contentV.setKey("content");
                            contentV.setValue(value);
                            contentV.setType(TMDLType.STRING);

                            indexValueList.add(contentV);

                            id[0] = StringTools.MD5Encode(value);
                            isRowkey[0] = true;
                        }

                        relationList.stream().filter(k -> k.getMsgkey().equals(key)).findFirst().map(l -> {

                            HBTableEntity hbTableEntity = l.getHbtable();

                            tableName[0] = hbTableEntity.getHbtableName();
                            isTowlevelIndex[0] = hbTableEntity.isHbtableIstwoLevelIndex();

                            String columnFamilyName = l.getHbcolumnfamilyName();
                            String columnName = l.getHbcolumnName();

                            Optional<HbaseColumnFamilyEntity> findFamily = hbaseColumnFamilyList
                                    .stream().filter(m -> m.getColumnFamilyName().equals(columnFamilyName)).findFirst();

                            HbaseColumnFamilyEntity familyEntity;

                            if (!findFamily.isPresent()) {
                                List<HbaseColumnEntity> columnList = new ArrayList<>();

                                familyEntity = new HbaseColumnFamilyEntity();
                                familyEntity.setColumnList(columnList);
                                familyEntity.setColumnFamilyName(columnFamilyName);

                                hbaseColumnFamilyList.add(familyEntity);
                            } else {
                                familyEntity = findFamily.get();
                            }

                            HbaseColumnEntity hbaseColumn = new HbaseColumnEntity();
                            hbaseColumn.setColumnName(columnName);
                            hbaseColumn.setColumnValue(value);

                            familyEntity.getColumnList().add(hbaseColumn);

                            if (isIndex(hbTableEntity, columnFamilyName, columnName)) {
                                String indexKey = columnFamilyName + "_" + columnName;

                                IndexValue indexValue = new IndexValue();
                                indexValue.setKey(indexKey);
                                indexValue.setValue(value);
                                indexValue.setType(valtype);


                                indexValueList.add(indexValue);
                            }
                            return true;
                        });
                    }
                });

                if (isRowkey[0] && StringUtils.isNotBlank(tableName[0])) {

                    hbaseRowEntity.setColumnFamily(hbaseColumnFamilyList);
                    hbRowList.add(hbaseRowEntity);

                    indexBody.setId(id[0]);
                    indexBody.setBody(indexValueList);

                    indexBodyList.add(indexBody);
                }
            });

            if (isPersistence[0]) {
                try {
                    if (hbRowList.size() > 0 && hbRowList.get(0).getColumnFamily().size() > 0) {

                        String ctableName = getTableName(tableName[0]);
                        TMDLHbOperator.insert(ctableName, hbRowList);

                        String indexName = getIndexName(tableName[0]);
                        if (isTowlevelIndex[0]) {

                            IndexMsg indexMsg = new IndexMsg();
                            indexMsg.setIndexName(indexName);
                            indexMsg.setIndexBodyList(indexBodyList);

                            String smsg = gson.toJson(indexMsg);

                            TMRabbitMqOperator.publishQuery(indexQuery, smsg);

                        }
                    }
                } catch (RetriesExhaustedWithDetailsException e) {
                    isSuccess[0] = true;
                    log.error(e.toString());
                } catch (IllegalArgumentException ex) {
                    isSuccess[0] = true;
                    log.error(ex.toString());
                } catch (NullPointerException ex) {
                    isSuccess[0] = true;
                    log.error(ex.toString());
                } catch (Exception ex) {
                    isSuccess[0] = false;
                    errorMsg[0] = ex.toString();
                    log.error(ex.toString());
                }
            }
            return true;
        });

        if (!isSuccess[0]) {
            throw new Exception(errorMsg[0]);
        }
    }

    private static String getTableName(String tableName) {
        return BusinessCache.tableSegmentList.stream()
                .filter(d -> d.getHtName().equals(tableName)).findFirst().get().getHtableSegment().getHbcurrenttablename();
    }

    private static String getIndexName(String tableName) {
        return BusinessCache.twoLevelIndexList.stream()
                .filter(d -> d.getHtName().equals(tableName)).findFirst().get().getHtableIndex().getHbcurrentindexname();
    }
}
