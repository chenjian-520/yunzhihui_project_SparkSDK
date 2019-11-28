package com.treasuremountain.datalake.dlapiservice.common.entity.business;

import javax.swing.text.StyledEditorKit;
import java.util.List;

/**
 * Created by gerryzhao on 10/21/2018.
 */
public class HBTableEntity {
    private String hbtableId;

    private String hbtableName;

    private Boolean hbtableIscompression;

    private String hbtableCompressionname;

    private Boolean hbtableIssplit;

    private String hbtableSplitinfo;

    private String hbtableDesc;

    private Boolean hbtableIstwoLevelIndex;

    private List<HBcolumnfamilyEntity> columnfamilyList;

    public String getHbtableId() {
        return hbtableId;
    }

    public void setHbtableId(String hbtableId) {
        this.hbtableId = hbtableId;
    }

    public String getHbtableName() {
        return hbtableName;
    }

    public void setHbtableName(String hbtableName) {
        this.hbtableName = hbtableName;
    }

    public Boolean getHbtableIscompression() {
        return hbtableIscompression;
    }

    public void setHbtableIscompression(Boolean hbtableIscompression) {
        this.hbtableIscompression = hbtableIscompression;
    }

    public String getHbtableCompressionname() {
        return hbtableCompressionname;
    }

    public void setHbtableCompressionname(String hbtableCompressionname) {
        this.hbtableCompressionname = hbtableCompressionname;
    }

    public Boolean getHbtableIssplit() {
        return hbtableIssplit;
    }

    public void setHbtableIssplit(Boolean hbtableIssplit) {
        this.hbtableIssplit = hbtableIssplit;
    }

    public String getHbtableSplitinfo() {
        return hbtableSplitinfo;
    }

    public void setHbtableSplitinfo(String hbtableSplitinfo) {
        this.hbtableSplitinfo = hbtableSplitinfo;
    }

    public String getHbtableDesc() {
        return hbtableDesc;
    }

    public void setHbtableDesc(String hbtableDesc) {
        this.hbtableDesc = hbtableDesc;
    }

    public Boolean isHbtableIstwoLevelIndex() {
        return hbtableIstwoLevelIndex;
    }

    public void setHbtableIstwoLevelIndex(Boolean hbtableIstwoLevelIndex) {
        this.hbtableIstwoLevelIndex = hbtableIstwoLevelIndex;
    }

    public List<HBcolumnfamilyEntity> getColumnfamilyList() {
        return columnfamilyList;
    }

    public void setColumnfamilyList(List<HBcolumnfamilyEntity> columnfamilyList) {
        this.columnfamilyList = columnfamilyList;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof HBTableEntity ){
            HBTableEntity old= (HBTableEntity)o;
            if(old.equals(this.hbtableId)){
                return true;
            }
        }
        return super.equals(o);
    }
}
