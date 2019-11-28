package com.tm.dl.javasdk.dpspark.hbase;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Description:  com.tm.dl.javasdk.dpspark.hbase
 * Copyright: © 2019 Foxconn. All rights reserved.
 * Company: Foxconn
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/20
 */
public class DPHbaseTest {

    public static void main(String[] args) {
        Put put = new Put(Bytes.toBytes("rowkey1"));
        put.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("col1"), Bytes.toBytes("100"));
        put.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("col2"), Bytes.toBytes("100"));
        put.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("col3"), Bytes.toBytes("100"));
        put.getFamilyCellMap().forEach((k, v) -> {
            v.stream().forEach(cell -> {
                String rowkey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                String columnFamily = Bytes.toString(cell.getFamilyArray());
                String columnQualifier = Bytes.toString(cell.getQualifierArray());
                String value = Bytes.toString(cell.getValueArray());
                System.out.println(rowkey + columnFamily + columnQualifier + value);
            });

        });
    }
}
