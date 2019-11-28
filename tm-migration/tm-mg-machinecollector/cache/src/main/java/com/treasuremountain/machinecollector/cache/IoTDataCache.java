package com.treasuremountain.machinecollector.cache;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class IoTDataCache {
    private String org;
    private List<Map<String, String>> iotData = new Vector<>();

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public List<Map<String, String>> getIotData() {
        return iotData;
    }

    public void setIotData(List<Map<String, String>> iotData) {
        this.iotData = iotData;
    }
}
