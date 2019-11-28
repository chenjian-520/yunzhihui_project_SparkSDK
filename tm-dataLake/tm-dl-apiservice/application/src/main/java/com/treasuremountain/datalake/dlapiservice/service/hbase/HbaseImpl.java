package com.treasuremountain.datalake.dlapiservice.service.hbase;


import com.treasuremountain.datalake.dlapiservice.controller.BaseController;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLHbOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseFilterParmMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseQueryColumnMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.HbaseTableMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by gerryzhao on 10/20/2018.
 */
@Component
public class HbaseImpl {
    @Autowired
    private BaseController baseController;

    public HbaseTableMsg queryTable(boolean isSalt, String tableName, List<HbaseQueryColumnMsg> columns,
                                    String startRowKey, String endRowKey, long showCount) throws IOException {
        if (isSalt) {
            return TMDLHbOperator.querySaltTable(tableName, columns, startRowKey, endRowKey, showCount, baseController.getCurrentUser());
        } else {
            return TMDLHbOperator.queryTable(tableName, columns, startRowKey, endRowKey, showCount, baseController.getCurrentUser());
        }
    }

    public HbaseTableMsg queryTableBatch(String tableName, List<HbaseQueryColumnMsg> columns,
                                         List<String> rowkeyList) throws IOException {
        return TMDLHbOperator.queryTableBatch(tableName, columns, rowkeyList, baseController.getCurrentUser());
    }

    public HbaseTableMsg queryTableFilter(boolean isSalt, int searchThreadCount, String tableName, List<HbaseQueryColumnMsg> columns, String startRowKey,
                                          String endRowKey, List<HbaseFilterParmMsg> filterParmMsgs, long showCount) throws IOException, ExecutionException, InterruptedException {
        if (isSalt) {
            return TMDLHbOperator.querySaltTableFilter(searchThreadCount, tableName, columns, startRowKey, endRowKey, filterParmMsgs, showCount, baseController.getCurrentUser());
        } else {
            return TMDLHbOperator.queryTableFilter(tableName, columns, startRowKey, endRowKey, filterParmMsgs, showCount, baseController.getCurrentUser());
        }
    }

}
