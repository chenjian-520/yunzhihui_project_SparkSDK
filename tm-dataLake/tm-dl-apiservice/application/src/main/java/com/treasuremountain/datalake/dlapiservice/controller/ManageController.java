package com.treasuremountain.datalake.dlapiservice.controller;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.common.data.business.BusinessConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.data.htable.*;
import com.treasuremountain.datalake.dlapiservice.common.data.query.ExchangeConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex.IndexlogDto;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TableSegmentEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.TwoLevelIndexEntity;
import com.treasuremountain.datalake.dlapiservice.common.message.HttpResponseMsg;
import com.treasuremountain.datalake.dlapiservice.config.webconfig.ApiVersion;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.business.BusinessImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.business.ExchangeImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.htable.HBtableImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.htable.RelationtableImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.htable.TableLogImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.*;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.twolevelindex.IndexlogImpl;
import com.treasuremountain.datalake.dlapiservice.service.initialization.InitializationService;
import com.treasuremountain.datalake.dlapiservice.service.manage.ManageService;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLHbOperator;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by gerryzhao on 10/22/2018.
 * 管理业务及hbase 表
 */
@RestController
@RequestMapping("/api/dlapiservice/{version}/")
public class ManageController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(ManageController.class);

    @Autowired
    private ManageService manageService;

    @Autowired
    private BusinessImpl businessImpl;

    @Autowired
    private ExchangeImpl exchangeImpl;

    @Autowired
    private RelationtableImpl relationtableImpl;

    @Autowired
    private HBtableImpl hBtableImpl;

    @Autowired
    private TableLogImpl tableLogImpl;

    @Autowired
    private IndexlogImpl indexlogImpl;
    private Gson gson=new Gson();

    //region Hbase
    //修改hbase表
    @RequestMapping(value = "/datalake/{businessId}", method = RequestMethod.PUT)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg createHbaseTable(@PathVariable("businessId") String businessId,
                                            @RequestHeader("User-Agent") String userAgent) throws Exception {
        try {

//            String logStr1 = TMlogMaker.dataFlow(businessId, getRsquestInfoStr() + userAgent,
//                    "", TMDataFlowStep.DLMakeModelReceived, "is received", 1);
//            log.info(logStr1);

            manageService.createHbaseTable(businessId);
            HttpResponseMsg httpResponseMsg = ResponseUtil.created(super.response, "created", null);

//            String logStr2 = TMlogMaker.dataFlow("", getRsquestInfoStr() + userAgent,
//                    "", TMDataFlowStep.DLMakeModelCreated, "is created", 1);
//            log.info(logStr2);

            return httpResponseMsg;
        } catch (NotFoundException ex) {
            return ResponseUtil.notFound(super.response, ex.toString(), null);
        }
    }
    //初始化hbase表
    @RequestMapping(value = "/datalake/{htableid}", method = RequestMethod.POST)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg createHbaseTableByHtableId(@PathVariable("htableid") String htableid,
                                                      @RequestHeader("User-Agent") String userAgent) throws Exception {
        try {
            manageService.createHbaseTableByhtableid(htableid);
            return ResponseUtil.created(super.response, "created", null);
        } catch (NotFoundException ex) {
            return ResponseUtil.notFound(super.response, ex.toString(), null);
        }
    }

    @RequestMapping(value = "/datalake/indexinfo", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public List<TwoLevelIndexEntity> getTwoLevelIndexInfo() {
        return BusinessCache.twoLevelIndexList;
    }

    @RequestMapping(value = "/datalake/tablesegmentinfo", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public List<TableSegmentEntity> getTableSegmentInfo() {
        return BusinessCache.tableSegmentList;
    }


    @RequestMapping(value = "/datalake/segment/{tableId}", method = RequestMethod.PUT)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg createSegment(@PathVariable("tableId") String tableId) throws IOException {
        manageService.createTable(tableId);
        HttpResponseMsg httpResponseMsg = ResponseUtil.created(super.response, "created", null);
        return httpResponseMsg;
    }

    @RequestMapping(value = "/datalake/index/{tableId}", method = RequestMethod.PUT)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg createIndex(@PathVariable("tableId") String tableId) throws Exception {
        manageService.createIndex(tableId);
        BusinessCache.loadTwoLevelIndexConfigByHtableid(tableId);//刷新index缓存
        HttpResponseMsg httpResponseMsg = ResponseUtil.created(super.response, "created", null);
        return httpResponseMsg;
    }

    @RequestMapping(value = "/datalake/index/{tableId}/{indexName}", method = RequestMethod.DELETE)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg deleteIndex(@PathVariable("tableId") String tableId,
                                       @PathVariable("indexName") String indexName) throws Exception {
        manageService.deleteIndex(tableId, indexName);
        BusinessCache.removeTwoLevelIndexConfigByHtableid(tableId);//add by ref.tian
        HttpResponseMsg httpResponseMsg = ResponseUtil.deleted(super.response, "deleted", null);
        return httpResponseMsg;
    }

    //endregion


    //region BusinessConfig
    //新增业务
    @RequestMapping(value = "/business", method = RequestMethod.POST, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg createBussinessConfig(@RequestBody BusinessConfigDto businessConfigDto) throws Exception {
        try {
            if (businessConfigDto.getBusinessId() == null || businessConfigDto.getBusinessId().isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "BusinessId不能为空！", null);
            }
         /*   if (businessConfigDto.getRelationtableConfigs() == null || businessConfigDto.getRelationtableConfigs().size() <= 0) {
                return ResponseUtil.internalServerError(super.response, "RelationtableConfigs不能为空！", null);
            }*/
            Integer count = businessImpl.insert(businessConfigDto);
            if (count > 0) {
                BusinessCache.loadCacheByid(businessConfigDto.getBusinessId());
                return ResponseUtil.created(super.response, "created", businessConfigDto.getBusinessId());
            } else {
                return ResponseUtil.internalServerError(super.response, "创建失败", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/business", method = RequestMethod.PUT, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg updateBussinessConfig(@RequestBody BusinessConfigDto businessConfigDto) throws Exception {
        try {
            if (businessConfigDto.getBusinessId() == null || businessConfigDto.getBusinessId().isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "BusinessId不能为空！", null);
            }
        /*    if (businessConfigDto.getRelationtableConfigs() == null || businessConfigDto.getRelationtableConfigs().size() <= 0) {
                return ResponseUtil.internalServerError(super.response, "RelationtableConfigs不能为空！", null);
            }*/
            Integer count = businessImpl.updateByPrimaryKey(businessConfigDto);
            if (count > 0) {
                BusinessCache.loadCacheByid(businessConfigDto.getBusinessId());
                return ResponseUtil.created(super.response, "created", true);
            } else {
                return ResponseUtil.internalServerError(super.response, "修改不成功", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/business/{bid}", method = RequestMethod.DELETE, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg deleteBussinessConfig(@PathVariable("bid") String bid) throws Exception {
        try {
            if (bid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "bid不能为空", null);
            }
            Integer count = businessImpl.deleteByPrimaryKey(bid);
            if (count > 0) {
                BusinessCache.removeCacheByid(bid);
                return ResponseUtil.deleted(super.response, "deleted", true);
            } else {
                return ResponseUtil.internalServerError(super.response, "删除不成功", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    @RequestMapping(value = "/business/{bid}", method = RequestMethod.GET, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getBusinessConfig(@PathVariable("bid") String bid) {
        try {
            if (bid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "bid不能为空", null);
            }
            BusinessConfigDto businessConfigDto = businessImpl.selectByPrimaryKey(bid);
            return ResponseUtil.ok(super.response, "selected", businessConfigDto);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/business/list", method = RequestMethod.PUT, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getBussinessConfigByPrimaryKeyList(@RequestBody List<String> list) throws Exception {
        try {
            if (list == null || list.size() <= 0) {
                return ResponseUtil.internalServerError(super.response, "list不能为空", null);
            }
            List<BusinessConfigDto> businessConfigDtos = businessImpl.selectByPrimaryKeyList(list);
            return ResponseUtil.ok(super.response, "selected", businessConfigDtos);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    //endregion

    //region Exchange

    //新增转发节点
    @RequestMapping(value = "/exchange", method = RequestMethod.POST, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg createExchangeConfig(@RequestBody ExchangeConfigDto exchangeConfigDto) throws Exception {
        try {

            if (exchangeConfigDto.getExchangeName() == null || exchangeConfigDto.getExchangeName().isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "ExchangeName不能为空！", null);
            }
            ExchangeConfigDo exchangeConfigDo = new ExchangeConfigDo();
            exchangeConfigDo.setExchangeId(UUID.randomUUID().toString());
            exchangeConfigDo.setExchangeName(exchangeConfigDto.getExchangeName());
            exchangeConfigDo.setExchangeIsenable(true);
            exchangeConfigDo.setExchangeModifiedby(exchangeConfigDto.getExchangeModifiedby());
            exchangeConfigDo.setExchangeModifieddt(new Date());
            exchangeConfigDo.setExchangeCreateby(exchangeConfigDto.getExchangeCreateby());
            exchangeConfigDo.setExchangeCreatedt(new Date());
            Integer count = exchangeImpl.insert(exchangeConfigDo);
            if (count > 0) {
                return ResponseUtil.created(super.response, "created", exchangeConfigDo.getExchangeId());
            } else {
                return ResponseUtil.internalServerError(super.response, "创建失败", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    @RequestMapping(value = "/exchanges", method = RequestMethod.GET, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getExchangesConfig() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        try {
            ExchangeConfigDo exchangeConfigDo = new ExchangeConfigDo();
            exchangeConfigDo.setExchangeIsenable(true);
            List<ExchangeConfigDo> exchangeConfigDos = exchangeImpl.selectSelective(exchangeConfigDo);
            if (exchangeConfigDos != null && exchangeConfigDos.size() > 0) {
                for (ExchangeConfigDo exdo :
                        exchangeConfigDos) {
                    hashMap.put(exdo.getExchangeId(), exdo.getExchangeName());
                }
            }
            return ResponseUtil.ok(super.response, "selected", hashMap);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    @RequestMapping(value = "/exchange/{eid}", method = RequestMethod.DELETE, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg deleteExchangeConfig(@PathVariable("eid") String eid) throws Exception {
        try {
            if (eid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "BusinessId不能为空！", null);
            }
            Integer count = exchangeImpl.deleteByPrimaryKey(eid);
            if (count > 0) {
                return ResponseUtil.deleted(super.response, "deleted", true);
            } else {
                return ResponseUtil.internalServerError(super.response, "删除不成功", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    //endregion

    //region relationtableConfig
    //获取当前业务下的表的mapping信息
    @RequestMapping(value = "/business/relationtables/{bid}", method = RequestMethod.GET, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getBusinessConfigRelationtables(@PathVariable("bid") String bid) {
        try {
            if (bid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "bid不能为空", null);
            }
            RelationtableConfigDo relationtableConfigDo = new RelationtableConfigDo();
            relationtableConfigDo.setBusinessId(bid);
            relationtableConfigDo.setRelationtableIsenable(true);
            List<RelationtableConfigDto> relationtableConfigDtos = relationtableImpl.selectSelective(relationtableConfigDo);
            return ResponseUtil.ok(super.response, "selected", relationtableConfigDtos);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    //endregion

    //region relationtableConfig
    // 新增hbase表
    @RequestMapping(value = "/hbaseconfig", method = RequestMethod.POST, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg createHbtableConfig(@RequestBody HBtableConfigDto hBtableConfigDto) throws Exception {
        try {
            log.debug("新增信息为："+gson.toJson(hBtableConfigDto));
            if (hBtableConfigDto.getHbtableId() == null || hBtableConfigDto.getHbtableId().isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "HbtableId不能为空！", null);
            }
            if (hBtableConfigDto.getColumnfamilyConfigDtos() == null) {
                return ResponseUtil.internalServerError(super.response, "columnfamilyConfigDtos不能为空！", null);
            }
            String msg = manageService.isValidDefault(hBtableConfigDto);
            if (msg != null) {
                return ResponseUtil.internalServerError(super.response, msg, null);
            }
            Integer count = hBtableImpl.insert(hBtableConfigDto);
            if (count > 0) {
                BusinessCache.loadHtableInfoByid(hBtableConfigDto.getHbtableId());
                return ResponseUtil.created(super.response, "created", hBtableConfigDto.getHbtableId());
            } else {
                return ResponseUtil.internalServerError(super.response, "创建失败", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/hbaseconfig/{hbid}", method = RequestMethod.PUT, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg updateHbaseconfig(@RequestBody HBtableConfigDto hBtableConfigDto) throws Exception {
        try {
            log.debug("修改信息为："+gson.toJson(hBtableConfigDto));
            if (hBtableConfigDto.getHbtableId() == null || hBtableConfigDto.getHbtableId().isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "HbtableId不能为空！", null);
            }
            if (hBtableConfigDto.getHbtableName() == null || hBtableConfigDto.getHbtableName().isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "HbtableName不能为空！", null);
            }
            String msg = manageService.isValidDefault(hBtableConfigDto);
            if (msg != null) {
                return ResponseUtil.internalServerError(super.response, msg, null);
            }
            Integer count = hBtableImpl.updateByPrimaryKey(hBtableConfigDto);
            if (count > 0) {
                BusinessCache.loadCacheByid(hBtableConfigDto.getHbtableId());// add by ref.tian
                return ResponseUtil.created(super.response, "created", true);
            } else {
                return ResponseUtil.internalServerError(super.response, "修改不成功", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    @RequestMapping(value = "/hbaseconfig/{hbid}", method = RequestMethod.DELETE, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg deleteHbaseconfig(@PathVariable("hbid") String hbid) throws Exception {
        try {
            if (hbid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "hbid不能为空", null);
            }
            Integer count = hBtableImpl.deleteByPrimaryKey(hbid);
            if (count > 0) {
                BusinessCache.removeHtableInfoByid(hbid);
                return ResponseUtil.deleted(super.response, "deleted", true);
            } else {
                return ResponseUtil.internalServerError(super.response, "删除不成功", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/datalake/htable/{tablename}", method = RequestMethod.DELETE, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg disableHbaseconfig(@PathVariable("tablename") String tablename) throws Exception {
        try {
            if (tablename.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "tablename不能为空", null);
            }
            TMDLHbOperator.disableTable(tablename,"hadoop");
            return ResponseUtil.deleted(super.response, "Disable成功", null);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/hbaseconfig/list", method = RequestMethod.PUT, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getHbaseconfigByPrimaryKeyList(@RequestBody List<String> list) throws Exception {
        try {
            if (list == null || list.size() <= 0) {
                return ResponseUtil.internalServerError(super.response, "list不能为空", null);
            }
            List<HBtableConfigDto> hBtableConfigDtos = hBtableImpl.selectByPrimaryKeyList(list);
            return ResponseUtil.ok(super.response, "selected", hBtableConfigDtos);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/hbaseconfig/{hbid}", method = RequestMethod.GET, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getHbaseconfigByPrimaryKey(@PathVariable("hbid") String hbid) throws Exception {
        try {
            if (hbid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "hbid不能为空", null);
            }
            List<String> strList = new ArrayList<>();
            strList.add(hbid);
            List<HBtableConfigDto> hBtableConfigDtos = hBtableImpl.selectByPrimaryKeyList(strList);
            HBtableConfigDto hBtableConfigDto = null;
            if (hBtableConfigDtos != null && hBtableConfigDtos.size() > 0) {
                hBtableConfigDto = hBtableConfigDtos.get(0);
            }
            return ResponseUtil.ok(super.response, "selected", hBtableConfigDto);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/hbaseconfig/tables/select", method = RequestMethod.PUT, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getHbaseconfigTablesSelect(@RequestBody List<String> list) throws Exception {
        try {
            if (list == null && list.size() <= 0) {
                return ResponseUtil.internalServerError(super.response, "list不能为空", null);
            }
            List<HbaseTablesInfoDto> hbaseTablesInfoDtos = hBtableImpl.getHbaseconfigTablesSelect(list);
            return ResponseUtil.ok(super.response, "selected", hbaseTablesInfoDtos);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    //获取当前table的历史索引历史表列族列等信息,Tableid：当前表id
    @RequestMapping(value = "/hbaseconfig/tables/select/{tableid}", method = RequestMethod.GET, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getHbaseconfigTablesSelectByTableid(@PathVariable("tableid") String tableid) throws Exception {
        try {
            if (tableid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "tableid", null);
            }
            List<String> list = new ArrayList<>();
            list.add(tableid);
            List<HbaseTablesInfoDto> hbaseTablesInfoDtos = hBtableImpl.getHbaseconfigTablesSelect(list);
            if (hbaseTablesInfoDtos != null && hbaseTablesInfoDtos.size() > 0) {
                List<HBtableConfigDto> hBtableConfigDto = hBtableImpl.selectByPrimaryKeyList(list);
                if (hBtableConfigDto != null && hBtableConfigDto.size() > 0) {
                    List<HBcolumnfamilyConfigDto> hBcolumnfamilyConfigDtos = hBtableConfigDto.get(0).getColumnfamilyConfigDtos();
                    HashMap<String, HBcolumnfamilyConfigDto> hBcolumnfamilyConfigDtoHashMap = new HashMap<>();
                    for (HBcolumnfamilyConfigDto columnfamiliyFor : hBcolumnfamilyConfigDtos) {
                        hBcolumnfamilyConfigDtoHashMap.put(columnfamiliyFor.getHbcolumnfamilyName(), columnfamiliyFor);
                    }
                    hbaseTablesInfoDtos.get(0).setHbColumnFamilys(hBcolumnfamilyConfigDtoHashMap);
                }
                return ResponseUtil.ok(super.response, "selected", hbaseTablesInfoDtos.get(0));
            } else {
                return ResponseUtil.ok(super.response, "selected", null);
            }
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    @RequestMapping(value = "/hbaseconfig/tables/{accountid}", method = RequestMethod.PUT, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getHbaseconfigTablesAccountid(@RequestBody List<String> list) throws Exception {
        try {
            if (list == null && list.size() <= 0) {
                return ResponseUtil.internalServerError(super.response, "list不能为空", null);
            }
            List<HbaseTablesInfoDto> hbaseTablesInfoDtos = hBtableImpl.getHbaseconfigTablesAccountid(list);
            return ResponseUtil.ok(super.response, "selected", hbaseTablesInfoDtos);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    //endregion

    //region tableLog


    @RequestMapping(value = "/hbaseconfig/table/old/{htableid}", method = RequestMethod.GET, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getHbaseconfigTableOld(@PathVariable("htableid") String htableid) throws Exception {
        //参数：Htableid table表id，必输栏位不能为空
        //逻辑：按照htableid查询表tablelog，按照tablelog_createtime进行排序
        try {
            if (htableid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "htableid不能为空", null);
            }
            TablelogDo tablelogDo = new TablelogDo();
            tablelogDo.setHbtableId(htableid);
            List<TableLogDto> tableLogDtos = tableLogImpl.selectSelective(tablelogDo);
            return ResponseUtil.ok(super.response, "selected", tableLogDtos);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    //endregion

    //region indexlog


    @RequestMapping(value = "/hbaseconfig/index/old/{htableid}", method = RequestMethod.GET, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getHbaseconfigIndexOld(@PathVariable("htableid") String htableid) throws Exception {
        //参数：Htableid table表id，必输栏位不能为空
        //逻辑：按照htableid查询表indexlog，按照indexlog_createtime进行排序
        try {
            if (htableid.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "htableid不能为空", null);
            }
            IndexlogDo indexlogDo = new IndexlogDo();
            indexlogDo.setHbtableId(htableid);
            List<IndexlogDto> indexlogDtos = indexlogImpl.selectSelective(indexlogDo);
            return ResponseUtil.ok(super.response, "selected", indexlogDtos);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    //endregion

}
