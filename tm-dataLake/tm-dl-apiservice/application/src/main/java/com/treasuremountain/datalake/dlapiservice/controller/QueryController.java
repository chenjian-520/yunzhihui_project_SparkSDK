package com.treasuremountain.datalake.dlapiservice.controller;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.SimpleDataInfo;
import com.treasuremountain.datalake.dlapiservice.common.message.HbaseSimpleMsg;
import com.treasuremountain.datalake.dlapiservice.config.webconfig.ApiVersion;
import com.treasuremountain.datalake.dlapiservice.service.query.QueryService;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.*;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMDataFlowStep;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMlogMaker;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gerryzhao on 10/17/2018.
 */
@RestController
@RequestMapping("/api/dlapiservice/{version}/")
public class QueryController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(QueryController.class);

    @Autowired
    private QueryService queryService;

    @Autowired
    private Gson gson;

    /* http://localhost:8089/api/dlapiservice/v1/hbpincloud/C0100021:ABCD:9223370497349815807/C0100021:ABCD:9223370497349875807/10 */

    @RequestMapping(value = "/{tableName}/{startRowKey}/{endRowKey}/{showCount}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg rowkeyQuery(@PathVariable("tableName") String tableName,
                                     @PathVariable("startRowKey") String startRowKey,
                                     @PathVariable("endRowKey") String endRowKey,
                                     @PathVariable("showCount") long showCount,
                                     @RequestHeader("User-Agent") String userAgent) throws Exception {

        return rowKeyQuery(false, tableName, null, startRowKey, endRowKey, showCount, userAgent);
    }


    @RequestMapping(value = "/{tableName}/{columns}/{startRowKey}/{endRowKey}/{showCount}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg rowkeyQueryColumn(@PathVariable("tableName") String tableName,
                                           @PathVariable("columns") String columns,
                                           @PathVariable("startRowKey") String startRowKey,
                                           @PathVariable("endRowKey") String endRowKey,
                                           @PathVariable("showCount") long showCount,
                                           @RequestHeader("User-Agent") String userAgent) throws Exception {

        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);

        return rowKeyQuery(false, tableName, columnMsgList, startRowKey, endRowKey, showCount, userAgent);
    }


    @RequestMapping(value = "/{tableName}/{columns}/{startRowKey}/{endRowKey}/{showCount}/{metadata}/{nsDelimiter}/{delimiter}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseSimpleMsg rowkeyQueryColumnSimple(@PathVariable("tableName") String tableName,
                                                  @PathVariable("columns") String columns,
                                                  @PathVariable("startRowKey") String startRowKey,
                                                  @PathVariable("endRowKey") String endRowKey,
                                                  @PathVariable("showCount") long showCount,
                                                  @PathVariable("metadata") boolean metadata,
                                                  @PathVariable("nsDelimiter") String nsDelimiter,
                                                  @PathVariable("delimiter") String delimiter,
                                                  @RequestHeader("User-Agent") String userAgent) throws Exception {

        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);

        HbaseTableMsg hbaseTableMsg = rowKeyQuery(false, tableName, columnMsgList, startRowKey, endRowKey, showCount, userAgent);
        return convertToSimpleData(columnMsgList, hbaseTableMsg, metadata, nsDelimiter, delimiter);
    }


    // equelValue 栏位等于查询columnFamilyName与columnName '-' 分隔，多个查询条件之间','分隔；如：fileinfo-filename=test.txt
    //regexValue 栏位正则表达式匹配columnFamilyName与columnName '-' 分隔，多个查询条件之间','分隔；如：fileinfo-filename=test.
    //如无需查询equelValue或regexValue用' '替代
    @RequestMapping(value = "/{tableName}/{startRowKey}/{endRowKey}/{equelValue}/{regexValue}/{showCount}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg queryTableFilter(@PathVariable("tableName") String tableName,
                                          @PathVariable("startRowKey") String startRowKey,
                                          @PathVariable("endRowKey") String endRowKey,
                                          @PathVariable("equelValue") String equelValue,
                                          @PathVariable("regexValue") String regexValue,
                                          @PathVariable("showCount") long showCount,
                                          @RequestHeader("User-Agent") String userAgent) throws Exception {
        return queryTableFilterF(false, tableName, null, startRowKey, endRowKey, equelValue, regexValue, showCount, userAgent);
    }

    @RequestMapping(value = "/{tableName}/{columns}/{startRowKey}/{endRowKey}/{equelValue}/{regexValue}/{showCount}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg queryTableFilterColumn(@PathVariable("tableName") String tableName,
                                                @PathVariable("columns") String columns,
                                                @PathVariable("startRowKey") String startRowKey,
                                                @PathVariable("endRowKey") String endRowKey,
                                                @PathVariable("equelValue") String equelValue,
                                                @PathVariable("regexValue") String regexValue,
                                                @PathVariable("showCount") long showCount,
                                                @RequestHeader("User-Agent") String userAgent) throws Exception {
        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);
        return queryTableFilterF(false, tableName, columnMsgList, startRowKey, endRowKey, equelValue, regexValue, showCount, userAgent);
    }

    @RequestMapping(value = "/{tableName}/{columns}/{startRowKey}/{endRowKey}/{equelValue}/{regexValue}/{showCount}/{metadata}/{nsDelimiter}/{delimiter}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseSimpleMsg queryTableFilterColumnSimple(@PathVariable("tableName") String tableName,
                                                       @PathVariable("columns") String columns,
                                                       @PathVariable("startRowKey") String startRowKey,
                                                       @PathVariable("endRowKey") String endRowKey,
                                                       @PathVariable("equelValue") String equelValue,
                                                       @PathVariable("regexValue") String regexValue,
                                                       @PathVariable("showCount") long showCount,
                                                       @PathVariable("metadata") boolean metadata,
                                                       @PathVariable("nsDelimiter") String nsDelimiter,
                                                       @PathVariable("delimiter") String delimiter,
                                                       @RequestHeader("User-Agent") String userAgent) throws Exception {
        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);
        HbaseTableMsg hbaseTableMsg = queryTableFilterF(false, tableName, columnMsgList, startRowKey, endRowKey, equelValue, regexValue, showCount, userAgent);
        return convertToSimpleData(columnMsgList, hbaseTableMsg, metadata, nsDelimiter, delimiter);
    }


    @RequestMapping(value = "/salt/{tableName}/{startRowKey}/{endRowKey}/{showCount}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg rowkeySaltQuery(@PathVariable("tableName") String tableName,
                                         @PathVariable("startRowKey") String startRowKey,
                                         @PathVariable("endRowKey") String endRowKey,
                                         @PathVariable("showCount") long showCount,
                                         @RequestHeader("User-Agent") String userAgent) throws Exception {

        return rowKeyQuery(true, tableName, null, startRowKey, endRowKey, showCount, userAgent);
    }


    @RequestMapping(value = "/salt/{tableName}/{columns}/{startRowKey}/{endRowKey}/{showCount}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg rowkeySaltQueryColumn(@PathVariable("tableName") String tableName,
                                               @PathVariable("columns") String columns,
                                               @PathVariable("startRowKey") String startRowKey,
                                               @PathVariable("endRowKey") String endRowKey,
                                               @PathVariable("showCount") long showCount,
                                               @RequestHeader("User-Agent") String userAgent) throws Exception {

        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);

        return rowKeyQuery(false, tableName, columnMsgList, startRowKey, endRowKey, showCount, userAgent);
    }


    @RequestMapping(value = "/salt/{tableName}/{columns}/{startRowKey}/{endRowKey}/{showCount}/{metadata}/{nsDelimiter}/{delimiter}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseSimpleMsg rowkeySaltQueryColumnSimple(@PathVariable("tableName") String tableName,
                                                      @PathVariable("columns") String columns,
                                                      @PathVariable("startRowKey") String startRowKey,
                                                      @PathVariable("endRowKey") String endRowKey,
                                                      @PathVariable("showCount") long showCount,
                                                      @PathVariable("metadata") boolean metadata,
                                                      @PathVariable("nsDelimiter") String nsDelimiter,
                                                      @PathVariable("delimiter") String delimiter,
                                                      @RequestHeader("User-Agent") String userAgent) throws Exception {

        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);

        HbaseTableMsg hbaseTableMsg = rowKeyQuery(true, tableName, columnMsgList, startRowKey, endRowKey, showCount, userAgent);
        return convertToSimpleData(columnMsgList, hbaseTableMsg, metadata, nsDelimiter, delimiter);
    }


    @RequestMapping(value = "/salt/{tableName}/{startRowKey}/{endRowKey}/{equelValue}/{regexValue}/{showCount}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg querySaltTableFilter(@PathVariable("tableName") String tableName,
                                              @PathVariable("startRowKey") String startRowKey,
                                              @PathVariable("endRowKey") String endRowKey,
                                              @PathVariable("equelValue") String equelValue,
                                              @PathVariable("regexValue") String regexValue,
                                              @PathVariable("showCount") long showCount,
                                              @RequestHeader("User-Agent") String userAgent) throws Exception {
        return queryTableFilterF(true, tableName, null, startRowKey, endRowKey, equelValue, regexValue, showCount, userAgent);
    }

    //filter
    @RequestMapping(value = "/salt/{tableName}/{columns}/{startRowKey}/{endRowKey}/{equelValue}/{regexValue}/{showCount}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg querySaltTableFilterColumn(@PathVariable("tableName") String tableName,
                                                    @PathVariable("columns") String columns,
                                                    @PathVariable("startRowKey") String startRowKey,
                                                    @PathVariable("endRowKey") String endRowKey,
                                                    @PathVariable("equelValue") String equelValue,
                                                    @PathVariable("regexValue") String regexValue,
                                                    @PathVariable("showCount") long showCount,
                                                    @RequestHeader("User-Agent") String userAgent) throws Exception {
        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);
        return queryTableFilterF(true, tableName, columnMsgList, startRowKey, endRowKey, equelValue, regexValue, showCount, userAgent);
    }

    @RequestMapping(value = "/salt/{tableName}/{columns}/{startRowKey}/{endRowKey}/{equelValue}/{regexValue}/{showCount}/{metadata}/{nsDelimiter}/{delimiter}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseSimpleMsg querySaltTableFilterColumnSimple(@PathVariable("tableName") String tableName,
                                                           @PathVariable("columns") String columns,
                                                           @PathVariable("startRowKey") String startRowKey,
                                                           @PathVariable("endRowKey") String endRowKey,
                                                           @PathVariable("equelValue") String equelValue,
                                                           @PathVariable("regexValue") String regexValue,
                                                           @PathVariable("showCount") long showCount,
                                                           @PathVariable("metadata") boolean metadata,
                                                           @PathVariable("nsDelimiter") String nsDelimiter,
                                                           @PathVariable("delimiter") String delimiter,
                                                           @RequestHeader("User-Agent") String userAgent) throws Exception {
        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);
        HbaseTableMsg hbaseTableMsg = queryTableFilterF(true, tableName, columnMsgList, startRowKey, endRowKey, equelValue, regexValue, showCount, userAgent);
        return convertToSimpleData(columnMsgList, hbaseTableMsg, metadata, nsDelimiter, delimiter);
    }

    /*http://localhost:8089/api/dlapiservice/v1/dpbu_mes/fileName like %27%e8%8a%b1%e6%9e%9c%e5%b1%b1%27*/

    @RequestMapping(value = "/{tableName}/{where}/sql", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg twoLevelQuery(@PathVariable("tableName") String tableName,
                                       @PathVariable("where") String where,
                                       @RequestHeader("User-Agent") String userAgent) throws Exception {

        return twoLevelQuery(tableName, null, where, userAgent);
    }


    @RequestMapping(value = "/{tableName}/{columns}/{where}/sql", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseTableMsg twoLevelQueryColumn(@PathVariable("tableName") String tableName,
                                             @PathVariable("columns") String columns,
                                             @PathVariable("where") String where,
                                             @RequestHeader("User-Agent") String userAgent) throws Exception {
        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);

        return twoLevelQuery(tableName, columnMsgList, where, userAgent);
    }

    //通过二级索引返回简易版数据格式，{"fmtInfo":{"metadata":true},"data":["user_info|sys_age_||_user_info|sys_name","2048_||_dp"]}
    //tableName hbase表明
    //tableName 需返回的栏位，columnfamily-column,columnfamily-column
    //where sql查询逻辑，bu='dpbu'
    //metadata 是否返回columnfamily及column name
    //nsDelimiter columnfamily与column之间的分隔符
    //delimiter 栏位与栏位之间的分隔符
    @RequestMapping(value = "/{tableName}/{columns}/{where}/sql/{metadata}/{nsDelimiter}/{delimiter}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseSimpleMsg twoLevelQueryColumnSimple(@PathVariable("tableName") String tableName,
                                                    @PathVariable("columns") String columns,
                                                    @PathVariable("where") String where,
                                                    @PathVariable("metadata") boolean metadata,
                                                    @PathVariable("nsDelimiter") String nsDelimiter,
                                                    @PathVariable("delimiter") String delimiter,
                                                    @RequestHeader("User-Agent") String userAgent) throws Exception {
        List<HbaseQueryColumnMsg> columnMsgList = makeColumnMsg(columns);

        HbaseTableMsg hbaseTableMsg = twoLevelQuery(tableName, columnMsgList, where, userAgent);
        return convertToSimpleData(columnMsgList, hbaseTableMsg, metadata, nsDelimiter, delimiter);
    }

    @RequestMapping(value = "/{tableName}/{where}/sql/{metadata}/{nsDelimiter}/{delimiter}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HbaseSimpleMsg twoLevelQueryColumnSimpleAllColumns(@PathVariable("tableName") String tableName,
                                                              @PathVariable("where") String where,
                                                              @PathVariable("metadata") boolean metadata,
                                                              @PathVariable("nsDelimiter") String nsDelimiter,
                                                              @PathVariable("delimiter") String delimiter,
                                                              @RequestHeader("User-Agent") String userAgent) throws Exception {

        HbaseTableMsg hbaseTableMsg = twoLevelQuery(tableName, null, where, userAgent);
        return convertToSimpleData(null, hbaseTableMsg, metadata, nsDelimiter, delimiter);
    }

    private HbaseTableMsg twoLevelQuery(String tableName, List<HbaseQueryColumnMsg> columnMsgList, String where, String userAgent) throws Exception {
        try {

//            String logStr1 = TMlogMaker.dataFlow("", getRsquestInfoStr() + userAgent,
//                    "", TMDataFlowStep.DLInsertRestReceived, "is received", 1);
//
//            log.info(logStr1);

            HbaseTableMsg hbaseTableMsg = queryService.twoLevelQuery(tableName, columnMsgList, where);

//            String logStr2 = TMlogMaker.dataFlow("", getRsquestInfoStr() + userAgent,
//                    gson.toJson(hbaseTableMsg), TMDataFlowStep.DLSearchRsetResulted, "is resulted", 1);
//
//            log.info(logStr2);

            return hbaseTableMsg;
        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }
    }

    private HbaseTableMsg rowKeyQuery(boolean isSalt, String tableName, List<HbaseQueryColumnMsg> columnMsgList, String startRowKey, String endRowKey,
                                      long showCount, String userAgent) throws Exception {
        try {

            String clientInfo = getRsquestInfoStr() + userAgent;

//            String logStr1 = TMlogMaker.dataFlow("", clientInfo,
//                    "", TMDataFlowStep.DLSearchRsetReceived, "is received", 1);
//            log.info(logStr1);

            HbaseTableMsg hbaseTableMsg = queryService.queryTable(isSalt, tableName, columnMsgList, startRowKey, endRowKey, showCount);

//            String logStr2 = TMlogMaker.dataFlow("", clientInfo,
//                    gson.toJson(hbaseTableMsg), TMDataFlowStep.DLSearchRsetResulted, "is resulted", 1);
//            log.info(logStr2);

            return hbaseTableMsg;

        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }
    }

    private HbaseTableMsg queryTableFilterF(boolean isSalt, String tableName, List<HbaseQueryColumnMsg> columnMsgList, String startRowKey, String endRowKey,
                                            String equelValue, String regexValue, long showCount, String userAgent) throws Exception {
        try {

//            String clientInfo = getRsquestInfoStr() + userAgent;

//            String logStr1 = TMlogMaker.dataFlow("", clientInfo,
//                    "", TMDataFlowStep.DLSearchRsetReceived, "is received", 1);
//            log.info(logStr1);

            List<HbaseFilterParmMsg> filterParmMsgs = new ArrayList<>();

            if (!equelValue.trim().isEmpty()) {
                Arrays.stream(equelValue.split(",")).forEach(d -> {
                    HbaseQueryColumnMsg hcm = analysisEquleColumn(d);

                    String value = analysisColumnValue(d);

                    HbaseFilterParmMsg msg = new HbaseFilterParmMsg();
                    msg.setColumnFamily(hcm.getColumnFamily());
                    msg.setColumn(hcm.getColumn());
                    msg.setValue(value);
                    msg.setType(HbaseFilterParmType.STRING);

                    filterParmMsgs.add(msg);
                });
            }

            if (!regexValue.trim().isEmpty()) {
                Arrays.stream(regexValue.split(",")).forEach(d -> {
                    HbaseQueryColumnMsg hcm = analysisEquleColumn(d);

                    String value = analysisColumnValue(d);

                    HbaseFilterParmMsg msg = new HbaseFilterParmMsg();
                    msg.setColumn(hcm.getColumn());
                    msg.setColumnFamily(hcm.getColumnFamily());
                    msg.setValue(value);
                    msg.setType(HbaseFilterParmType.REGEX);

                    filterParmMsgs.add(msg);
                });
            }

            HbaseTableMsg hbaseTableMsg = queryService.queryTableFilter(isSalt, tableName, columnMsgList, startRowKey, endRowKey, filterParmMsgs, showCount);

//            String logStr2 = TMlogMaker.dataFlow("", clientInfo,
//                    gson.toJson(hbaseTableMsg), TMDataFlowStep.DLSearchRsetResulted, "is resulted", 1);
//            log.info(logStr2);

            return hbaseTableMsg;

        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }
    }

    private List<HbaseQueryColumnMsg> makeColumnMsg(String columns) {

        if (columns != null) {
            List<HbaseQueryColumnMsg> columnMsgList = new ArrayList<>();

            String[] columnsArry = columns.split(",");
            for (String column : columnsArry) {
                HbaseQueryColumnMsg hqc = analysisColumn(column);
                if (hqc != null) {
                    columnMsgList.add(hqc);
                }
            }

            return columnMsgList;
        } else {
            return null;
        }
    }

    private String analysisColumnValue(String parm) {
        int ep = parm.indexOf('=');
        int parml = parm.length();

        return parm.substring(ep + 1, parml);
    }

    private HbaseQueryColumnMsg analysisColumn(String column) {
        String[] cf = column.split("-");

        if (cf.length > 1) {
            HbaseQueryColumnMsg hqc = new HbaseQueryColumnMsg();
            hqc.setColumnFamily(cf[0]);
            hqc.setColumn(cf[1]);

            return hqc;
        } else {
            return null;
        }
    }

    private HbaseQueryColumnMsg analysisEquleColumn(String column) {
        int ep = column.indexOf('=');
        String realColumn = column.substring(0, ep);
        return analysisColumn(realColumn);
    }

    private HbaseSimpleMsg convertToSimpleData(List<HbaseQueryColumnMsg> columnMsgList, HbaseTableMsg tableMsg, boolean metadata, String nsDelimiter, String delimiter) {

        HbaseSimpleMsg hbaseSimpleMsg = new HbaseSimpleMsg();

        SimpleDataInfo simpleDataInfo = new SimpleDataInfo();
        simpleDataInfo.setMetadata(metadata);

        hbaseSimpleMsg.setFmtInfo(simpleDataInfo);
        hbaseSimpleMsg.setIsNextPage(tableMsg.getIsNextPage());

        List<String> data = new ArrayList<>();

        List<HbaseRowEntity> rowList = tableMsg.getRowList();

        if (columnMsgList != null) {
            if (metadata) {
                if (rowList.size() > 0) {

                    StringBuilder hsb = new StringBuilder();

                    HbaseRowEntity rowEntity = rowList.get(0);
                    List<HbaseColumnFamilyEntity> hbaseColumnFamilyEntityList = rowEntity.getColumnFamily();
                    int columnSize = columnMsgList.size();

                    for (int i = 0; i < columnSize; i++) {
                        HbaseQueryColumnMsg queryColumnMsg = columnMsgList.get(i);

                        String columnFamily = queryColumnMsg.getColumnFamily();
                        String column = queryColumnMsg.getColumn();

                        int finalI = i;
                        hbaseColumnFamilyEntityList.stream().filter(f -> f.getColumnFamilyName().equals(columnFamily)).forEach(g -> {
                            List<HbaseColumnEntity> columnEntityList = g.getColumnList();
                            columnEntityList.stream().filter(h -> h.getColumnName().equals(column)).findFirst().map(k -> {
                                hsb.append(columnFamily);
                                hsb.append(nsDelimiter);
                                hsb.append(column);
                                if (finalI + 1 < columnSize) {
                                    hsb.append(delimiter);
                                }
                                return true;
                            });
                        });
                    }
                    data.add(hsb.toString());
                }
            }

            rowList.forEach(d -> {

                StringBuilder rsb = new StringBuilder();

                List<HbaseColumnFamilyEntity> hbaseColumnFamilyEntityList = d.getColumnFamily();

                int columnSize = columnMsgList.size();

                for (int i = 0; i < columnSize; i++) {
                    HbaseQueryColumnMsg queryColumnMsg = columnMsgList.get(i);

                    String columnFamily = queryColumnMsg.getColumnFamily();
                    String column = queryColumnMsg.getColumn();

                    int finalI = i;
                    hbaseColumnFamilyEntityList.stream().filter(f -> f.getColumnFamilyName().equals(columnFamily)).forEach(g -> {
                        List<HbaseColumnEntity> columnEntityList = g.getColumnList();
                        columnEntityList.stream().filter(h -> h.getColumnName().equals(column)).findFirst().map(k -> {
                            String value = k.getColumnValue();
                            rsb.append(value);
                            if (finalI + 1 < columnSize) {
                                rsb.append(delimiter);
                            }
                            return true;
                        });
                    });
                }
                data.add(rsb.toString());
            });
        } else {
            if (metadata) {
                if (rowList.size() > 0) {

                    StringBuilder hsb = new StringBuilder();

                    HbaseRowEntity rowEntity = rowList.get(0);
                    List<HbaseColumnFamilyEntity> hbaseColumnFamilyEntityList = rowEntity.getColumnFamily();
                    int columnFamilySize = hbaseColumnFamilyEntityList.size();

                    for (int l = 0; l < columnFamilySize; l++) {
                        HbaseColumnFamilyEntity familyEntity = hbaseColumnFamilyEntityList.get(l);
                        String columnFamilyName = familyEntity.getColumnFamilyName();
                        List<HbaseColumnEntity> columnEntityList = familyEntity.getColumnList();
                        int columnSize = columnEntityList.size();
                        for (int i = 0; i < columnSize; i++) {
                            HbaseColumnEntity entity = columnEntityList.get(i);
                            String columnName = entity.getColumnName();
                            hsb.append(columnFamilyName);
                            hsb.append(nsDelimiter);
                            hsb.append(columnName);
                            if (i + 1 < columnSize) {
                                hsb.append(delimiter);
                            }
                        }

                        if (l + 1 < columnFamilySize) {
                            hsb.append(delimiter);
                        }
                    }

                    data.add(hsb.toString());
                }
            }

            rowList.forEach(d -> {

                StringBuilder rsb = new StringBuilder();

                List<HbaseColumnFamilyEntity> hbaseColumnFamilyEntityList = d.getColumnFamily();
                int columnFamilySize = hbaseColumnFamilyEntityList.size();

                for (int l = 0; l < columnFamilySize; l++) {
                    HbaseColumnFamilyEntity familyEntity = hbaseColumnFamilyEntityList.get(l);
                    List<HbaseColumnEntity> columnEntityList = familyEntity.getColumnList();
                    int columnSize = columnEntityList.size();
                    for (int i = 0; i < columnSize; i++) {
                        HbaseColumnEntity entity = columnEntityList.get(i);
                        String columnValue = entity.getColumnValue();

                        rsb.append(columnValue);
                        if (i + 1 < columnSize) {
                            rsb.append(delimiter);
                        }
                    }

                    if (l + 1 < columnFamilySize) {
                        rsb.append(delimiter);
                    }
                }

                data.add(rsb.toString());
            });
        }

        hbaseSimpleMsg.setData(data);

        return hbaseSimpleMsg;
    }
}
