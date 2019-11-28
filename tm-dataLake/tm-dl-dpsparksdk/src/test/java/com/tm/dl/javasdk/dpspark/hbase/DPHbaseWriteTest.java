package com.tm.dl.javasdk.dpspark.hbase;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.streaming.DPStreaming;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Description:  com.tm.dl.javasdk.dpspark.hbase
 * Copyright: © 2019 Foxconn. All rights reserved.
 * Company: Foxconn
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/20
 */
public class DPHbaseWriteTest extends DPSparkBase {

    @Override
    public void scheduling(Map<String, Object> arrm) throws Exception {
//        Scan scan = new Scan();
//        List puts = new ArrayList();
//        Put put = new Put(Bytes.toBytes("01:1574240324248:sys_spark_index:" + UUID.randomUUID().toString().replaceAll("-", "")));
//        put.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("name"), Bytes.toBytes("bobwok"));
//        put.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("age"), Bytes.toBytes("tbdkj"));
//        puts.add(put);
//        DPHbase.rddWrite("sys_spark_index_test20191120142425", DPSparkApp.getContext().parallelize(puts));
//        DPSparkApp.stop();

        boolean b = DPHbaseHelper.javaForHbase("123", "chen20191121134125", "family");
        System.out.println(b);
        System.out.println(System.currentTimeMillis());

        Scan scan = new Scan();
        List puts = new ArrayList();
        Put put = new Put(Bytes.toBytes("01:1574240324248:sys_spark_index:" + UUID.randomUUID().toString().replaceAll("-", "")));
        put.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("name"), Bytes.toBytes("bobwo11"));
        put.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("age"), Bytes.toBytes("tbdkj"));
        puts.add(put);
        DPHbase.rddWrite("sys_spark_index_test20191120142425", DPSparkApp.getContext().parallelize(puts));
        DPSparkApp.stop();
    }

    @Override
    public void streaming(Map<String, Object> arrm, DPStreaming dpStreaming) throws Exception {

    }
}
