package com.treasuremountain.machinecollector.common.message;

import com.treasuremountain.machinecollector.common.entity.KepwareValueEntity;

import java.util.List;

public class KepwareIoTMsg {
    private long timestamp;
    private String org;
    private String reserveValue;
    private List<KepwareValueEntity> values;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getReserveValue() {
        return reserveValue;
    }

    public void setReserveValue(String reserveValue) {
        this.reserveValue = reserveValue;
    }

    public List<KepwareValueEntity> getValues() {
        return values;
    }

    public void setValues(List<KepwareValueEntity> values) {
        this.values = values;
    }
}
