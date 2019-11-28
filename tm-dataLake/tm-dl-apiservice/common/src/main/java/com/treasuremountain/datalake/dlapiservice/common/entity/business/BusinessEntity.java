package com.treasuremountain.datalake.dlapiservice.common.entity.business;

import java.util.List;

/**
 * Created by gerryzhao on 10/21/2018.
 */
public class BusinessEntity {
    private String businessId;

    private String businessName;

    private String businessDesc;

    private List<RelationEntity> relationList;

    private ExchangeEntity exchange;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public List<RelationEntity> getRelationList() {
        return relationList;
    }

    public void setRelationList(List<RelationEntity> relationList) {
        this.relationList = relationList;
    }

    public ExchangeEntity getExchange() {
        return exchange;
    }

    public void setExchange(ExchangeEntity exchange) {
        this.exchange = exchange;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof BusinessEntity ){
            BusinessEntity old= (BusinessEntity)o;
            if(old.equals(this.businessId)){
                return true;
            }
        }
        return super.equals(o);
    }
}
