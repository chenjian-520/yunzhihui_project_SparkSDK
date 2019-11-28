package com.treasuremountain.machinecloudreceiver.cache;

import com.treasuremountain.machinecloudreceiver.common.data.DatarelationConfigDto;
import com.treasuremountain.machinecloudreceiver.dao.datarelation.DatarelationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TimerTask;

@Component
public class DatarelationCache {
    private final static Logger log = LoggerFactory.getLogger(DatarelationCache.class);

    public static java.util.Timer timer = new java.util.Timer(true);
    public volatile static List<DatarelationConfigDto> DatarelationList;


    public static void loadCache() {
        DatarelationImpl datarelation = new DatarelationImpl();
        DatarelationList = datarelation.findAllDatarelation();
    }

    public static void autoRefreshCache() {
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    loadCache();
                } catch (Exception ex) {
                    log.error(ex.toString());
                }
            }
        };
        timer.schedule(task, 0, 10000);
    }
}
