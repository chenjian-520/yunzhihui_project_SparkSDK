package com.treasuremountain.datalake.sparksqlproxy;

import java.io.Serializable;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/6/17.
 * Company: Foxconn
 * Project: TreasureMountain
 */
public class SparkSqlTbaleDto implements Serializable {
    private String tableName;
    private String isSalt;
    private String startRowKey;
    private String endRowKey;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIsSalt() {
        return isSalt;
    }

    public void setIsSalt(String isSalt) {
        this.isSalt = isSalt;
    }

    public String getStartRowKey() {
        return startRowKey;
    }

    public void setStartRowKey(String startRowKey) {
        this.startRowKey = startRowKey;
    }

    public String getEndRowKey() {
        return endRowKey;
    }

    public void setEndRowKey(String endRowKey) {
        this.endRowKey = endRowKey;
    }
}
