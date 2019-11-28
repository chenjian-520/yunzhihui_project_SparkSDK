package com.treasuremountain.datalake.sparksqlproxy;

import org.apache.spark.sql.types.StructField;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TableSchemaEntity implements Serializable {
    private String tableName;
    private List<StructField> structFields;
    private ConcurrentHashMap<String, List<StructField>> structFamilyFields;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<StructField> getStructFields() {
        return structFields;
    }

    public void setStructFields(List<StructField> structFields) {
        this.structFields = structFields;
    }

    public void setStructFamilyFields(ConcurrentHashMap<String, List<StructField>> structFamilyFields) {
        this.structFamilyFields = structFamilyFields;
    }

    public ConcurrentHashMap<String, List<StructField>> getStructFamilyFields(){
        return this.structFamilyFields;
    }


}