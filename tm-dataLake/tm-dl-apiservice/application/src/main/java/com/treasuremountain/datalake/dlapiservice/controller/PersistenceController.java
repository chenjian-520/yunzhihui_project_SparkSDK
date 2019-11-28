package com.treasuremountain.datalake.dlapiservice.controller;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.RelationEntity;
import com.treasuremountain.datalake.dlapiservice.common.message.*;
import com.treasuremountain.datalake.dlapiservice.config.webconfig.ApiVersion;
import com.treasuremountain.datalake.dlapiservice.service.initialization.DataTools;
import com.treasuremountain.datalake.dlapiservice.service.persistence.PersistenceService;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMDataFlowStep;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMlogMaker;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gerryzhao on 10/21/2018.
 * 存储数据进 hbase
 */
@RestController
@RequestMapping("/api/dlapiservice/{version}/")
public class PersistenceController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(PersistenceController.class);

    @Autowired
    private Gson gson;

    @Autowired
    private PersistenceService persistenceService;

    // 存数据进 hbase
    @RequestMapping(value = "/{businessId}", method = RequestMethod.POST)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg persistence(@RequestBody BusinessDataMsg dataMsg,
                                       @RequestHeader("User-Agent") String userAgent) throws Exception {
        String businessId = dataMsg.getBusinessId();
        List<RowKv> rowKvs = dataMsg.getRowKvList();

        if (StringUtils.isNotBlank(businessId)
                && rowKvs != null
                && rowKvs.size() > 0
                && rowKvs.get(0).getColumnKvList() != null
                && rowKvs.get(0).getColumnKvList().size() > 0) {
            try {

//                String strDataMsg = gson.toJson(dataMsg);

//                String logStr1 = TMlogMaker.dataFlow(businessId, getRsquestInfoStr() + userAgent,
//                        strDataMsg, TMDataFlowStep.DLInsertRestReceived, "is received", 1);
//                log.info(logStr1);

                persistenceService.persistence(dataMsg);

//                String logStr2 = TMlogMaker.dataFlow(businessId, getRsquestInfoStr() + userAgent,
//                        strDataMsg, TMDataFlowStep.DLInsertRestReceived, "successed", 1);
//                log.info(logStr2);

                return ResponseUtil.created(super.response, "created", null);
            } catch (NotFoundException ex) {
                return ResponseUtil.notFound(super.response, ex.toString(), null);
            } catch (Exception ex) {
                String error = ex.toString();
                log.error(error);
                return ResponseUtil.internalServerError(super.response, error, null);
            }
        } else {
            return ResponseUtil.badRequest(super.response, "Parameter anomaly", null);
        }
    }

    /**
     *
     * 存数据进 hbase v2 ref.tian
     */
    @RequestMapping(value = "/{businessId}", method = RequestMethod.POST)
    @ApiVersion(2)
    @CrossOrigin
    public HttpResponseMsg persistence2(@RequestBody BusinessDataMsg dataMsg,
                                        @RequestHeader("User-Agent") String userAgent,
                                        @RequestHeader("X-Authorization") String usertoken) throws Exception {
        if (userAgent.trim().isEmpty()) {
            return ResponseUtil.badRequest(super.response, "缺少用户验证token", null);
        }
        String businessId = dataMsg.getBusinessId();
        List<RowKv> rowKvs = dataMsg.getRowKvList();

        if (StringUtils.isNotBlank(businessId)
                && rowKvs != null
                && rowKvs.size() > 0
                && rowKvs.get(0).getColumnKvList() != null
                && rowKvs.get(0).getColumnKvList().size() > 0) {
            try {
                ConcurrentHashMap<String, RelationEntity> concurrentHashMap = BusinessCache.businessColumnType.get(businessId);
                for (RowKv rowKv : rowKvs) {
                    for (ColumnKv columnKv : rowKv.getColumnKvList()) {
                        if(columnKv.getKey().equals("rowkey")){
                            continue;
                        }
                        RelationEntity relationEntity = concurrentHashMap.get(columnKv.getKey());
                        if (relationEntity != null) {
                            if (relationEntity.getHbcolumnType().equals(columnKv.getType().toString())) {
                                try {
                                    if (!DataTools.checkDataType(columnKv.getValue(), columnKv.getType().toString())) {
                                        return ResponseUtil.badRequest(super.response,
                                                "数据类型异常,数据需要的类型为：" + gson.toJson(relationEntity.getHbcolumnType()) + ",异常数据为：" + gson.toJson(columnKv),
                                                null);
                                    }
                                } catch (NumberFormatException ex) {
                                    return ResponseUtil.badRequest(super.response,
                                            "数据类型异常,数据需要的类型为：" + gson.toJson(relationEntity.getHbcolumnType()) + ",异常数据为：" + gson.toJson(columnKv),
                                            null);
                                }
                            } else {
                                return ResponseUtil.badRequest(super.response,
                                        "数据类型异常,数据需要的类型为：" + gson.toJson(relationEntity.getHbcolumnType()) + ",异常数据为：" + gson.toJson(columnKv),
                                        null);
                            }
                        } else {
                            // todo 没有找到mapping的数据直接拒绝
                            return ResponseUtil.badRequest(super.response,
                                    "没有找到该消息的映射配置，配置信息为：" + gson.toJson(relationEntity.getHbcolumnType()) + ",异常数据为：" + gson.toJson(columnKv),
                                    null);
                        }
                    }
                }
                persistenceService.persistence(dataMsg);
                return ResponseUtil.created(super.response, "created", null);
            } catch (NotFoundException ex) {
                return ResponseUtil.notFound(super.response, ex.toString(), null);
            } catch (Exception ex) {
                String error = ex.toString();
                log.error(error);
                return ResponseUtil.internalServerError(super.response, error, null);
            }
        } else {
            return ResponseUtil.badRequest(super.response, "Parameter anomaly", null);
        }
    }

    /* {
        "businessId": "b553e300-d5a9-11e8-938d-00505682ad31",
        "rowKvList": [{
            "columnKvList": [
              {
                "key": "rowkey",
                "value": "testrowkey5",
                "type": "STRING"
              },
              {
                "key": "filename",
                "value": "花果山wbs5.xlsx",
                "type": "STRING"
              },
              {
                "key": "filesize",
                "value": "10245",
                "type": "LONG"
              },
              {
                "key": "usercode",
                "value": "C01000215",
                "type": "STRING"
              },
              {
                "key": "username",
                "value": "gerryzhao5",
                "type": "STRING"
              }
            ]
        }]
    } */
}
