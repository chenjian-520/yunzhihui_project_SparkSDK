package com.treasuremountain.datalake.dlapiservice.common.entity.business;

import java.util.List;

public class TableSegmentEntity {

    private String htid;

    private String htName;

    private HtableSegmentEntity htableSegment;

    private List<TableLogEntity> tableLogList;

    public String getHtid() {
        return htid;
    }

    public void setHtid(String htid) {
        this.htid = htid;
    }

    public String getHtName() {
        return htName;
    }

    public void setHtName(String htName) {
        this.htName = htName;
    }

    public HtableSegmentEntity getHtableSegment() {
        return htableSegment;
    }

    public void setHtableSegment(HtableSegmentEntity htableSegment) {
        this.htableSegment = htableSegment;
    }

    public List<TableLogEntity> getTableLogList() {
        return tableLogList;
    }

    public void setTableLogList(List<TableLogEntity> tableLogList) {
        this.tableLogList = tableLogList;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TableSegmentEntity) {
            TableSegmentEntity old = (TableSegmentEntity) o;
            if (old.equals(this.htid)) {
                return true;
            }
        }
        return super.equals(o);
    }
}
