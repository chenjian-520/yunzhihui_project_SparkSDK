package com.treasuremountain.datalake.dlapiservice.service.persistence;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.cache.business.BusinessCache;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.BusinessEntity;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.ExchangeEntity;
import com.treasuremountain.datalake.dlapiservice.common.message.BusinessDataMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMDataFlowStep;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMlogMaker;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.tools.StringTools;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by gerryzhao on 10/21/2018.
 */
@Service
public class PersistenceService {

    private final static Logger log = LoggerFactory.getLogger(PersistenceService.class);

    @Value("${hbaseMasterQuery.queryName}")
    private String hbMasterName;

    @Value("${hbaseMasterQuery.isBackup}")
    private boolean hbIsBackup;

    @Value("${hbaseSlave1Query.queryName}")
    private String hbSlave1Name;

    @Value("${hbaseSlave2Query.queryName}")
    private String hbSlave2Name;

    @Autowired
    private Gson gson;

    public void persistence(BusinessDataMsg dataMsg) throws Exception {

        long count = BusinessCache.businesList.stream().filter(d -> d.getBusinessId().equals(dataMsg.getBusinessId())).count();
        if (count > 0) {
            pushToDL(dataMsg);
            pushToThirdParty(dataMsg);
        } else {
            throw new NotFoundException("business id not found!");
        }
    }

    private void pushToDL(BusinessDataMsg dataMsg) throws Exception {

        String businessId = dataMsg.getBusinessId();
        String msg = gson.toJson(dataMsg);

        try {
            TMRabbitMqOperator.publishQuery(hbMasterName, msg);

//            String logStr = TMlogMaker.dataFlow(businessId, "", msg, TMDataFlowStep.DLInsertSendToQuery,
//                    "has been sent master hb query", 1);
//
//            log.info(logStr);

            if (hbIsBackup) {
                int hash = StringTools.toHash(businessId);

                if (hash < 0) {
                    hash = hash * -1;
                }

                if (hash % 2 == 1) {
                    TMRabbitMqOperator.publishQuery(hbSlave1Name, msg);

//                    String slogStr = TMlogMaker.dataFlow(businessId, "", msg, TMDataFlowStep.DLInsertSendToQuery,
//                            "has been sent slave1 hb query", 1);
//
//                    log.info(slogStr);
                } else if (hash % 2 == 0) {
                    TMRabbitMqOperator.publishQuery(hbSlave2Name, msg);

//                    String slogStr = TMlogMaker.dataFlow(businessId, "", msg, TMDataFlowStep.DLInsertSendToQuery,
//                            "has been sent slave2 hb query", 1);
//
//                    log.info(slogStr);
                }
            }
        } catch (Exception ex) {
            String slogStr = TMlogMaker.dataFlow(businessId, "", msg, TMDataFlowStep.DLInsertSendToQuery, ex.toString(), 0);
            log.info(slogStr);
            log.error(ex.toString());
            throw ex;
        }
    }

    private void pushToThirdParty(BusinessDataMsg dataMsg) throws IOException {

        String businessId = dataMsg.getBusinessId();

        String msg = gson.toJson(dataMsg);

        Optional<BusinessEntity> opBusinessEntity = BusinessCache.businesList.stream().filter(d -> d.getBusinessId().equals(businessId)).findFirst();
        ExchangeEntity exchangeEntity = opBusinessEntity.get().getExchange();
        String exchangeName = exchangeEntity.getExchangeName();
        if (StringUtils.isNotBlank(exchangeName)) {
            TMRabbitMqOperator.publishExchange(exchangeName, msg);
        }
    }
}
