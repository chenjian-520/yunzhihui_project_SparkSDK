package com.tm.dl.javasdk.dpspark.hbase;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @Author:cj
 * @Description:TODO
 * @timestamp:2019/11/18 14.32
 */
@Slf4j
public class DPHbaseHelper {
    /**
     * @param rowkey
     * @param tablename
     * @param position
     * @return
     */

    public static boolean javaForHbase(String rowkey, String tablename, String position) {

        //构建Hbase的一行记录
        String dpuserid = DPSparkApp.getDpuserid() + "_standard_position";
        long logdt = System.currentTimeMillis();
        List puts = new ArrayList();
        Put put = new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes("fr"), Bytes.toBytes("tablename"), Bytes.toBytes(tablename));
        put.addColumn(Bytes.toBytes("fr"), Bytes.toBytes("position"), Bytes.toBytes(position));
        put.addColumn(Bytes.toBytes("fr"), Bytes.toBytes("logdt"), Bytes.toBytes(logdt));
        puts.add(put);

        try {
            DPHbase.rddWrite(dpuserid, DPSparkApp.getContext().parallelize(puts));
            return true;
        } catch (IOException e) {
            log.error("数据写入异常："+e.getMessage(),e);
        } catch (InterruptedException e) {
            log.error("数据写入异常："+e.getMessage(),e);
        }
        return false;
    }

}

