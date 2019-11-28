package com.treasuremountain.datalake.dlapiservice.common.entity.business;

import java.util.List;

public class TwoLevelIndexEntity {

    private String htid;

    private String htName;

    private HtableIndexEntity htableIndex;

    private List<IndexlogEntity> indexlogList;

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

    public HtableIndexEntity getHtableIndex() {
        return htableIndex;
    }

    public void setHtableIndex(HtableIndexEntity htableIndex) {
        this.htableIndex = htableIndex;
    }

    public List<IndexlogEntity> getIndexlogList() {
        return indexlogList;
    }

    public void setIndexlogList(List<IndexlogEntity> indexlogList) {
        this.indexlogList = indexlogList;
    }


    @Override
    public boolean equals(Object o){
        if(o instanceof TwoLevelIndexEntity ){
            TwoLevelIndexEntity old= (TwoLevelIndexEntity)o;
            if(old.equals(this.htid)){
                return true;
            }
        }
        return super.equals(o);
    }
}
