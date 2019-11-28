package com.tm.dl.javasdk.dpspark.mysql;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.entity.DBConnectionInfo;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import javax.swing.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:ref.tian
 * @Description: mysql操作类
 * @timestamp:2019/11/14 14:11
 **/
public class DPMysql {

    /**
     * 读取mysql数据
     *
     * @param dburl      sqlserver 数据库连接：jdbc:sqlserver://127.0.0.1:1433;databaseName=DataLake;loginTimeout=90;
     * @param dbuser     数据库账号
     * @param dbPassword 数据库密码
     * @param query      查询语句
     **/
    public static JavaRDD<Row> rddRead(String dburl, String dbuser, String dbPassword, String query) {
        SparkSession sparkSession = DPSparkApp.getSession();

        Dataset<Row> dataset = sparkSession.read().format("jdbc")
                .option("url", dburl)
                .option("driver", "com.mysql.jdbc.Driver")
                .option("user", dbuser)
                .option("password", dbPassword)
                .option("query", query)
                .load();
        return dataset.toJavaRDD();
    }

    /**
     * @param query 查询语句
     *              查询默认db中的数据
     **/
    public static JavaRDD<Row> rddRead(String query) {
        DBConnectionInfo dbConnectionInfo = DPSparkApp.getDpPermissionManager().getMysqlInfo();
        return rddRead(dbConnectionInfo.getUrl(), dbConnectionInfo.getUsername(), dbConnectionInfo.getPassword(), query);
    }

    /**
     * 批量插入数据到mysql，使用odbc写入，适合海量数据写入
     ***/
    public static void commonOdbcWriteBatch(String dburl, String dbuser, String dbPassword, String tablename,
                                            JavaRDD<Row> insertdata, HashMap<String, StructField> dbcolums, StructType rowAgeNameSchema) {
        SparkSession sparkSession = DPSparkApp.getSession();
        Dataset<Row> insertdataDs = sparkSession.createDataFrame(insertdata, rowAgeNameSchema);
//        insertdataDs.show();
        //分区处理
        insertdataDs.foreachPartition(new ForeachPartitionFunction<Row>() {
            @Override
            public void call(Iterator<Row> iterator) throws Exception {
                Driver sqlserverdriver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
                Properties dbpro = new Properties();
                dbpro.put("user", dbuser);
                dbpro.put("password", dbPassword);
                Connection con = null;
                try {
                    con = sqlserverdriver.connect(dburl, dbpro);
                    con.setAutoCommit(true);
                    String sql = "INSERT INTO " + tablename + "(  ";
                    String values = "values ( ";
                    Iterator iter = dbcolums.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = entry.getKey().toString();
                        if (!iter.hasNext()) {
                            sql += key + " ) ";
                            values += "? ) ";
                        } else {
                            sql += key + ",";
                            values += "?,";
                        }
                    }
                    sql += values;
                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    //插入计数器
                    AtomicInteger cout = new AtomicInteger(0);

                    while (iterator.hasNext()) {
                        // 循环数据
                        Row p = iterator.next();
                        iter = dbcolums.entrySet().iterator();
                        int i = 0;
                        while (iter.hasNext()) {
                            Map.Entry ent = (Map.Entry) iter.next();
                            preparedStatement.setString(i + 1, p.getAs(ent.getKey().toString()));
                            i++;
                        }
                        preparedStatement.addBatch();
                        //循环，每5000笔做一次提交
                        if (cout.addAndGet(1) >= 2000) {
                            cout.set(0);
                            preparedStatement.executeBatch();
                        }
//                    System.out.println(p.get(0).toString());
                    }
                    // 批量提交
                    try {
                        preparedStatement.executeBatch();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    } finally {
                        preparedStatement.close();
                        con.close();
                    }
                } catch (Exception e) {
                    System.out.println("sql server connect error:" + e.getMessage());
                }
            }
        });
    }

    /**
     * 批量插入数据到到默认的db，使用odbc写入，适合海量数据写入
     * ***/
    public static void commonOdbcWriteBatch(String tablename,
                                            JavaRDD<Row> insertdata, HashMap<String, StructField> dbcolums, StructType rowAgeNameSchema) {

        DBConnectionInfo dbConnectionInfo = DPSparkApp.getDpPermissionManager().getMysqlInfo();
        commonOdbcWriteBatch(dbConnectionInfo.getUrl(), dbConnectionInfo.getUsername(), dbConnectionInfo.getPassword(),
                tablename, insertdata, dbcolums, rowAgeNameSchema);
    }

    /**
     * 直接使用dataset写入，无法使用在海量数据
     **/
    public static void commonDatasetWriteBatch(String dburl, String dbuser, String dbPassword, String tablename,
                                               JavaRDD<Row> insertdata, List<StructField> fieldList, SaveMode saveMode) {
        SparkSession sparkSession = DPSparkApp.getSession();
        StructType rowAgeNameSchema = DataTypes.createStructType(fieldList);
        Dataset<Row> insertdataDs = sparkSession.createDataFrame(insertdata, rowAgeNameSchema);
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", dbuser);
        connectionProperties.put("password", dbPassword);
        insertdataDs.write().mode(saveMode).jdbc(dburl, tablename, connectionProperties);

    }

    /***
     * 批量存储数据到默认的mysql
     * **/
    public static void commonDatasetWriteBatch(String tablename,
                                               JavaRDD<Row> insertdata, List<StructField> fieldList, SaveMode saveMode){

        DBConnectionInfo dbConnectionInfo = DPSparkApp.getDpPermissionManager().getMysqlInfo();
        commonDatasetWriteBatch(dbConnectionInfo.getUrl(), dbConnectionInfo.getUsername(), dbConnectionInfo.getPassword(),
                tablename, insertdata,fieldList,saveMode);

    }
}
