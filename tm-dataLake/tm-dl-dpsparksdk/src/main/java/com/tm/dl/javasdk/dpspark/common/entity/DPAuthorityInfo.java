package com.tm.dl.javasdk.dpspark.common.entity;

import java.util.Map;

public class DPAuthorityInfo {
    private Map<String, String> hbaseAuthinfo;
    private Map<String, String> hdfsAuthinfo;

    public Map<String, String> getHbaseAuthinfo() {
        return hbaseAuthinfo;
    }

    public void setHbaseAuthinfo(Map<String, String> hbaseAuthinfo) {
        this.hbaseAuthinfo = hbaseAuthinfo;
    }

    public Map<String, String> getHdfsAuthinfo() {
        return hdfsAuthinfo;
    }

    public void setHdfsAuthinfo(Map<String, String> hdfsAuthinfo) {
        this.hdfsAuthinfo = hdfsAuthinfo;
    }
}
