package com.treasuremountain.datalake.dlapiservice.controller;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBtableConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.message.HttpResponseMsg;
import com.treasuremountain.datalake.dlapiservice.config.webconfig.ApiVersion;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.htable.HBtableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description:  com.treasuremountain.datalake.dlapiservice.controller
 * Copyright: © 2019 Foxconn. All rights reserved.
 * Company: Foxconn
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/21
 */
@RestController
@RequestMapping("/api/common/{version}/")
public class CommonController extends BaseController {
    private final static Logger log = LoggerFactory.getLogger(ManageController.class);
    @Autowired
    private HBtableImpl hBtableImpl;

    /**
     * 根据真实表名查询hbaseConfig信息
     *
     * @param tablename
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/hbaseconfig/realtable/{tablename}", method = RequestMethod.GET, consumes = "application/json")
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg getIndexByRealName(@PathVariable("tablename") String tablename) throws Exception {
        //参数：Htableid table表id，必输栏位不能为空
        //逻辑：按照htableid查询表indexlog，按照indexlog_createtime进行排序
        try {
            if (tablename.isEmpty()) {
                return ResponseUtil.internalServerError(super.response, "currentTableName不能为空", null);
            }
            HBtableConfigDto result = hBtableImpl.selectByRealTableName(tablename);
            return ResponseUtil.ok(super.response, "selected", result);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }
}
