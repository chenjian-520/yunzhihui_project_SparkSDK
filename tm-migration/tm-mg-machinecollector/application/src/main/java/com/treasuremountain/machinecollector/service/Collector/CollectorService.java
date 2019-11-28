package com.treasuremountain.machinecollector.service.Collector;

import com.google.gson.Gson;
import com.treasuremountain.machinecollector.cache.IoTDataCache;
import com.treasuremountain.machinecollector.common.entity.KepwareValueEntity;
import com.treasuremountain.machinecollector.common.message.KepwareIoTMsg;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.TMRabbitMqOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CollectorService {

    @Value("${migrationquery.cloud}")
    private String migrationquery_cloud;

    @Autowired
    private Gson gson;

    public void kpreceiver(KepwareIoTMsg dataMsg) throws IOException {
        KepwareIoTMsg rdata = cleaningRepetition(dataMsg);
        if (rdata.getValues().size() > 0) {
            TMRabbitMqOperator.publishQuery(migrationquery_cloud, gson.toJson(rdata));
        }
    }

    private KepwareIoTMsg cleaningRepetition(KepwareIoTMsg dataMsg) {
        List<KepwareValueEntity> values = dataMsg.getValues();
        values.forEach(d -> {
            String key = d.getId();
            String value = d.getV();
            String cachevs = IoTDataCache.getIoTData(key);
            if (cachevs != null && cachevs.equals(value)) {
                KepwareValueEntity entity = values.stream().filter(f -> f.getId().equals(value)).findFirst().get();
                values.remove(entity);
            } else {
                IoTDataCache.setIotData(key, value);
            }
        });

        return dataMsg;
    }
}
