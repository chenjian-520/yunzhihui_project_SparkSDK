package com.tm.dl.javasdk.dpspark.common.entity;

import java.util.List;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/11/14 16:13
 **/
public class ExternalInfoDto {

    public List<String> getHbaseExt() {
        return hbaseExt;
    }

    public void setHbaseExt(List<String> hbaseExt) {
        this.hbaseExt = hbaseExt;
    }

    public List<String> getMysqlExt() {
        return mysqlExt;
    }

    public void setMysqlExt(List<String> mysqlExt) {
        this.mysqlExt = mysqlExt;
    }

    private List<String> hbaseExt;
    private List<String> mysqlExt;
}
