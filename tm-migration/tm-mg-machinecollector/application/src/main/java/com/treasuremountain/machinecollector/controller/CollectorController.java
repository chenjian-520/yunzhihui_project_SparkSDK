package com.treasuremountain.machinecollector.controller;

import com.google.gson.Gson;
import com.treasuremountain.machinecollector.common.message.HttpResponseMsg;
import com.treasuremountain.machinecollector.common.message.KepwareIoTMsg;
import com.treasuremountain.machinecollector.config.webconfig.ApiVersion;
import com.treasuremountain.machinecollector.service.collector.CollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machinecollector/{version}/")
public class CollectorController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(CollectorController.class);

    @Autowired
    private Gson gson;

    @Autowired
    private CollectorService collectorService;

    @RequestMapping(value = "/kpreceiver", method = RequestMethod.POST)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg kpreceiver(@RequestBody KepwareIoTMsg dataMsg,
                                      @RequestHeader("User-Agent") String userAgent) throws Exception {

//        String clientInfo = getRsquestInfoStr() + userAgent;
//        String msg = gson.toJson(dataMsg);
//        String logStr1 = TMlogMaker.dataFlow("", clientInfo,
//                msg, TMDataFlowStep.MGkpReceived, "is received", 1);
//        log.info(logStr1);

        collectorService.kpreceiver(dataMsg);

//        String logStr2 = TMlogMaker.dataFlow("", clientInfo,
//                msg, TMDataFlowStep.MGkpSendToQuery, "is sent", 1);
//        log.info(logStr2);

        return ResponseUtil.created(super.response, "created", null);
    }

    /*
      {
        "timestamp": 1544061137503,
        "values": [{
            "id": "通道 2.设备 1.T1",
            "v": 5796,
            "q": true,
            "t": 1544061128178
        }, {
            "id": "通道 2.设备 1.T1",
            "v": 5816,
            "q": true,
            "t": 1544061129349
        }, {
            "id": "通道 2.设备 1.T1",
            "v": 5835,
            "q": true,
            "t": 1544061130529
        }, {
            "id": "通道 2.设备 1.T1",
            "v": 5855,
            "q": true,
            "t": 1544061131694
        }, {
            "id": "通道 2.设备 1.T1",
            "v": 5874,
            "q": true,
            "t": 1544061132860
        }, {
            "id": "通道 2.设备 1.T1",
            "v": 5894,
            "q": true,
            "t": 1544061134033
        }, {
            "id": "通道 2.设备 1.T1",
            "v": 5913,
            "q": true,
            "t": 1544061135199
        }, {
            "id": "通道 2.设备 1.T1",
            "v": 5933,
            "q": true,
            "t": 1544061136379
        }]
    }
    */
}
