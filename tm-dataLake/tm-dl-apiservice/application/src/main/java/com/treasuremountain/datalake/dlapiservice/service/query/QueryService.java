package com.treasuremountain.datalake.dlapiservice.service.query;

import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.IndexlogEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TableLogEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TableSegmentEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TwoLevelIndexEntity;
import com.treasuremountain.datalake.dlapiservice.service.hbase.HbaseImpl;
import com.treasuremountain.datalake.dlapiservice.service.initialization.InitializationService;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.message.MTSearchContentMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseFilterParmMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseQueryColumnMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseTableMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by gerryzhao on 10/17/2018.
 */
@Service
public class QueryService {

    @Autowired
    private HbaseImpl hbase;

    public HbaseTableMsg queryTable(boolean isSalt, String tableName, List<HbaseQueryColumnMsg> columns,
                                    String startRowKey, String endRowKey, long showCount) throws Exception {
        final String[] errorMsg = {""};

        HbaseTableMsg rhbm = new HbaseTableMsg();
        rhbm.setTableName(tableName);
        rhbm.setShowCount(showCount);
        rhbm.setRowList(new ArrayList<>());

        List<TableLogEntity> tle = getTableLogList(tableName);

        new ForkJoinPool(InitializationService.sSearchThreadCount).submit(() -> {

            tle.parallelStream().forEachOrdered(d -> {
                try {
                    String segTableName = d.getTablelogName();
                    HbaseTableMsg rh = hbase.queryTable(isSalt, segTableName, columns, startRowKey, endRowKey, showCount);
                    rhbm.getRowList().addAll(rh.getRowList());
                } catch (Exception ex) {
                    errorMsg[0] = ex.toString();
                }
            });

        }).get();

        rhbm.setIsNextPage("not support");

        if (StringUtils.isNotBlank(errorMsg[0])) {
            throw new Exception(errorMsg[0]);
        }

        return rhbm;
    }

    public HbaseTableMsg twoLevelQuery(String tableName, List<HbaseQueryColumnMsg> columns, String where) throws Exception {

        final String[] errorMsg = {""};

        List<String> indexs = new ArrayList<>();
        List<IndexlogEntity> ile = getIndexList(tableName);
        ile.forEach(d -> {
            indexs.add(d.getIndexlogName());
        });

        String fullSQL = String.format("SELECT content FROM %s WHERE %s", indexs.get(0), where);
        String dsl = MTElasticsearchOperator.translate(fullSQL);

        MTSearchContentMsg searchContentMsg = MTElasticsearchOperator.queryDSL(indexs, dsl);

        if (StringUtils.isBlank(errorMsg[0])) {

            List<String> rowkeys = new ArrayList<>();

            searchContentMsg.getRows().forEach(d -> {
                rowkeys.add(d.get(0));
            });

            HbaseTableMsg rhbm = new HbaseTableMsg();
            rhbm.setTableName(tableName);
            rhbm.setShowCount(0);
            rhbm.setIsNextPage("not support");
            rhbm.setRowList(new ArrayList<>());

            List<TableLogEntity> tle = getTableLogList(tableName);

            new ForkJoinPool(InitializationService.sSearchThreadCount).submit(() -> {

                tle.parallelStream().forEachOrdered(d -> {
                    try {
                        String segTableName = d.getTablelogName();
                        HbaseTableMsg rh = hbase.queryTableBatch(segTableName, columns, rowkeys);
                        rhbm.getRowList().addAll(rh.getRowList());
                    } catch (Exception ex) {
                        errorMsg[0] = ex.toString();
                    }
                });

            }).get();

            if (StringUtils.isNotBlank(errorMsg[0])) {
                throw new Exception(errorMsg[0]);
            }

            return rhbm;
        } else {
            throw new Exception(errorMsg[0]);
        }
    }

    public HbaseTableMsg queryTableFilter(boolean isSalt, String tableName, List<HbaseQueryColumnMsg> columns, String startRowKey,
                                          String endRowKey, List<HbaseFilterParmMsg> filterParmMsgs, long showCount) throws Exception {

        final String[] errorMsg = {""};

        HbaseTableMsg rhbm = new HbaseTableMsg();
        rhbm.setTableName(tableName);
        rhbm.setShowCount(showCount);
        rhbm.setIsNextPage("not support");
        rhbm.setRowList(new ArrayList<>());

        List<TableLogEntity> tle = getTableLogList(tableName);

        new ForkJoinPool(InitializationService.sSearchThreadCount).submit(() -> {

            tle.parallelStream().forEachOrdered(d -> {
                try {
                    String segTableName = d.getTablelogName();
                    HbaseTableMsg rh = hbase.queryTableFilter(isSalt, InitializationService.sSearchThreadCount, segTableName, columns, startRowKey, endRowKey, filterParmMsgs, showCount);
                    rhbm.getRowList().addAll(rh.getRowList());
                } catch (Exception ex) {
                    errorMsg[0] = ex.toString();
                }
            });

        }).get();

        if (StringUtils.isNotBlank(errorMsg[0])) {
            throw new Exception(errorMsg[0]);
        }

        return rhbm;
    }

    private List<IndexlogEntity> getIndexList(String tableName) {
        Optional<TwoLevelIndexEntity> twoLevelIndexEntity = BusinessCache.twoLevelIndexList.stream().filter(d -> d.getHtName().equals(tableName)).findFirst();
        if (twoLevelIndexEntity.isPresent()) {
            return twoLevelIndexEntity.get().getIndexlogList();
        } else {
            return new ArrayList<>();
        }
    }

    private List<TableLogEntity> getTableLogList(String tableName) {
        Optional<TableSegmentEntity> tableSegmentEntity = BusinessCache.tableSegmentList.stream().filter(d -> d.getHtName().equals(tableName)).findFirst();
        if (tableSegmentEntity.isPresent()) {
            return tableSegmentEntity.get().getTableLogList();
        } else {
            return new ArrayList<>();
        }
    }


}
