package com.treasuremountain.datalake.dlapiservice.service.manage;

import com.alibaba.fastjson.JSON;
import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.common.data.business.BusinessConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBcolumnfamilyConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBtableConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.data.htable.HbaseTablesInfoDto;
import com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex.IndexlogDto;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.*;
import com.treasuremountain.datalake.dlapiservice.controller.BaseController;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.business.BusinessImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.htable.HBtableImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.htable.TableLogImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.BusinessConfigDo;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.twolevelindex.IndexlogImpl;
import com.treasuremountain.datalake.dlapiservice.service.initialization.InitializationService;
import com.treasuremountain.tmcommon.thirdpartyservice.elasticsearch.MTElasticsearchOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLHbOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLObjOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.tools.StringTools;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache.businesList;
import static com.treasuremountain.datalake.dlapiservice.service.initialization.InitializationService.maxHttpClientUtils;

/**
 * Created by gerryzhao on 10/22/2018.
 */
@Service
@Slf4j
public class ManageService {


    @Autowired
    private BusinessImpl businessImpl;

    @Autowired
    private HBtableImpl hBtable;

    @Autowired
    private IndexlogImpl indexlog;

    @Autowired
    private TableLogImpl tableLog;
    @Autowired
    private BaseController baseController;


    //region    Hbase
    public void createHbaseTable(String businessId) throws Exception {

        Optional<BusinessEntity> opBusinessEntity = businesList.stream().filter(
                d -> d.getBusinessId().equals(businessId)).findFirst();
        if (opBusinessEntity.isPresent()) {
            BusinessEntity businessEntity = opBusinessEntity.get();
            List<RelationEntity> relationEntityList = businessEntity.getRelationList();
            RelationEntity relationEntity = relationEntityList.get(0);

            HBTableEntity hbTableEntity = relationEntity.getHbtable();
            String tableName = hbTableEntity.getHbtableName();
            String tableId = hbTableEntity.getHbtableId();
            if (StringTools.regexMatch("^[a-z:_]+$", tableName)) {
                createTable(tableId);
                createIndex(tableId);
            } else {
                throw new NotFoundException("can not find business");
            }
        } else {
            throw new NotFoundException("can not find business");
        }
    }

    /**
     * 通过htableid新建表
     **/
    public void createHbaseTableByhtableid(String htableid) throws Exception {

        HBtableConfigDto hBtableConfigDto = hBtable.selectAllInfoByPrimaryKey(htableid);
        if (hBtableConfigDto != null) {
            if (StringTools.regexMatch("^[a-z:_]+$", hBtableConfigDto.getHbtableName())) {
                createTable(hBtableConfigDto);
                createIndex(hBtableConfigDto);
                try {
                    hBtable.updateInittag(htableid);
                } catch (Exception e) {
                    log.error("更新hbase 初始化状态异常：" + htableid, e);
                }

            } else {
                throw new Exception("Htable name is invalid!");
            }
        } else {
            throw new Exception("Htable can not find it!");
        }
    }

    public void createTable(String tableId) throws IOException {

        HBTableEntity hbTableEntity = BusinessCache.hbTableList.stream().filter(d -> d.getHbtableId().equals(tableId)).findFirst().get();

        String tableName = hbTableEntity.getHbtableName();
        String compressionType = hbTableEntity.getHbtableCompressionname();

        List<HBcolumnfamilyEntity> hBcolumnfamilyEntityList = hbTableEntity.getColumnfamilyList();
        String[] columnFamilies = new String[hBcolumnfamilyEntityList.size()];
        for (int i = 0; i < hBcolumnfamilyEntityList.size(); i++) {
            columnFamilies[i] = hBcolumnfamilyEntityList.get(i).getHbcolumnfamilyName();
        }
        byte[][] splitKeys = new byte[0][];
        String splitInfo = hbTableEntity.getHbtableSplitinfo();

        if (StringUtils.isNotBlank(splitInfo)) {
            String[] splitInfoArry = splitInfo.split("|");
            splitKeys = new byte[splitInfoArry.length][];
            for (int i = 0; i < splitInfoArry.length; i++) {
                splitKeys[i] = Bytes.toBytes(splitInfoArry[i]);
            }
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        tableName = tableName.replace(":", "_") + df.format(new Date());
        String currentUserId = baseController.getCurrentUser();
        TMDLHbOperator.createTable(tableName, columnFamilies, splitKeys, compressionType, currentUserId);

        HBtableImpl hbimpl = new HBtableImpl();
        hbimpl.updateCurrentTableName(tableId, tableName);

        TableLogImpl tblimpl = new TableLogImpl();
        tblimpl.addTableLog(tableId, tableName);

        TMDLObjOperator objOperator = new TMDLObjOperator(InitializationService.sBackupQuorum, InitializationService.sbackupClientPort);
        objOperator.createTable(tableName, columnFamilies, splitKeys, compressionType);

//        BusinessCache.loadTableSegmentConfByHtableid(tableId);//add ref.tian
    }

    /***
     * 根据hbase配置信息初始化hbase表
     * **/
    public void createTable(HBtableConfigDto hBtableConfigDto) throws IOException {

        String tableName = hBtableConfigDto.getHbtableName();
        String compressionType = hBtableConfigDto.getHbtableCompressionname();

        List<HBcolumnfamilyConfigDto> hBcolumnfamilyEntityList = hBtableConfigDto.getColumnfamilyConfigDtos();
        String[] columnFamilies = new String[hBcolumnfamilyEntityList.size()];
        for (int i = 0; i < hBcolumnfamilyEntityList.size(); i++) {
            columnFamilies[i] = hBcolumnfamilyEntityList.get(i).getHbcolumnfamilyName();
        }
        byte[][] splitKeys = new byte[0][];
        String splitInfo = hBtableConfigDto.getHbtableSplitinfo();

        if (StringUtils.isNotBlank(splitInfo)) {
            String[] splitInfoArry = splitInfo.split("\\|");
            splitKeys = new byte[splitInfoArry.length][];
            for (int i = 0; i < splitInfoArry.length; i++) {
                splitKeys[i] = Bytes.toBytes(splitInfoArry[i]);
            }
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        tableName = tableName.replace(":", "_") + df.format(new Date());
        String currentUser = baseController.getCurrentUser();
        TMDLHbOperator.createTable(tableName, columnFamilies, splitKeys, compressionType, currentUser);

        hBtable.updateCurrentTableName(hBtableConfigDto.getHbtableId(), tableName);

        tableLog.addTableLog(hBtableConfigDto.getHbtableId(), tableName);
        // todo 测试时可以将这里屏蔽
        TMDLObjOperator objOperator = new TMDLObjOperator(InitializationService.sBackupQuorum, InitializationService.sbackupClientPort);
        objOperator.createTable(tableName, columnFamilies, splitKeys, compressionType);

        //add ref.tian
//        BusinessCache.loadTableSegmentConfByHtableid(hBtableConfigDto.getHbtableId());
    }

    public void createIndex(String tableId) throws Exception {

        TwoLevelIndexEntity indexEntity = BusinessCache.twoLevelIndexList
                .stream().filter(d -> d.getHtableIndex().getHbtableId().equals(tableId)).findFirst().get();

        String tableName = indexEntity.getHtName();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        tableName = tableName.replace(":", "_") + df.format(new Date());

        MTElasticsearchOperator.createIndex(tableName, "hb");

        HBtableImpl hbimpl = new HBtableImpl();
        hbimpl.updateCurrentIndex(tableId, tableName);

        IndexlogImpl indexlogimpl = new IndexlogImpl();
        indexlogimpl.addIndexlog(tableId, tableName);


    }

    /*
     **
     * 根据hbase表信息初始化索引
     * **/
    public void createIndex(HBtableConfigDto hBtableConfigDto) throws Exception {

        String tableName = hBtableConfigDto.getHbtableName();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        tableName = tableName.replace(":", "_") + df.format(new Date());

        MTElasticsearchOperator.createIndex(tableName, "hb");
        hBtable.updateCurrentIndex(hBtableConfigDto.getHbtableId(), tableName);
        indexlog.addIndexlog(hBtableConfigDto.getHbtableId(), tableName);
    }

    public void deleteIndex(String tableId, String indexName) throws Exception {
        MTElasticsearchOperator.deleteIndex(indexName);

        IndexlogImpl indexlogimpl = new IndexlogImpl();
        indexlogimpl.deleteIndexlog(tableId, indexName);
    }

    //endregion


    //region Config

    //校验对象是否有空数据
    public String isValidDefault(HBtableConfigDto record) {
        String msg = null;
        String nullMsg = "不能等于空,";
        if (record.getHbtableIscompression() == null) {
            msg += "Iscompression" + nullMsg;
        }
        if (record.getHbtableIstablesegment() == null) {
            msg += "Istablesegment" + nullMsg;
        }
        if (record.getHbtableIstwoLevelIndex() == null) {
            msg += "IstwoLevelIndex" + nullMsg;
        }

        if (record.getHbtableSplitinfo() == null || record.getHbtableSplitinfo().isEmpty()) {
            record.setHbtableIssplit(false);
        } else {
            record.setHbtableIssplit(true);
        }

        return msg;
    }
    //endregion


}
