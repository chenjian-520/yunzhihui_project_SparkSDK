package com.treasuremountain.datalake.sparksqlproxy;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/6/17.
 * Company: Foxconn
 * Project: TreasureMountain
 */
public class SparkSqlParamsDto implements Serializable {
    private List<SparkSqlTbaleDto> tables;
    private String sql;
    private String mqName;

    public List<SparkSqlTbaleDto> getTables() {
        return tables;
    }

    public void setTables(List<SparkSqlTbaleDto> tables) {
        this.tables = tables;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getMqName() {
        return mqName;
    }

    public void setMqName(String mqName) {
        this.mqName = mqName;
    }
}
