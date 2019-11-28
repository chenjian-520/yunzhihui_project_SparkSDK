package com.treasuremountain.tmcommon.thirdpartyservice.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableSplit;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DPSaltTableInputFormat extends TableInputFormat {

    @Override
    public List<InputSplit> getSplits(JobContext context) throws IOException {
        Configuration conf = getConf();
        String table = conf.get(TableInputFormat.INPUT_TABLE);

        TableName tableName = TableName.valueOf(table);

        Connection connection = ConnectionFactory.createConnection(conf);
        RegionLocator regionLocator = connection.getRegionLocator(tableName);

        String scanStart = conf.get(TableInputFormat.SCAN_ROW_START);
        String scanStop = conf.get(TableInputFormat.SCAN_ROW_STOP);

        Pair<byte[][], byte[][]> keys = regionLocator.getStartEndKeys();

        byte[][] firstKey = keys.getFirst();

        List<InputSplit> splits = new ArrayList<>(firstKey.length);

        for (int i = 0; i < firstKey.length; i++) {
            byte[] fb = firstKey[i];
            HRegionLocation rl = regionLocator.getRegionLocation(fb);

            String regionSalt = null;
            if (firstKey[i].length > 0) {
                regionSalt = Bytes.toString(firstKey[i]);
            } else {
                regionSalt = "00";
            }
            byte[] startRowKey = Bytes.toBytes(regionSalt + ":" + scanStart);
            byte[] endRowKey = Bytes.toBytes(regionSalt + ":" + scanStop);

            InputSplit split = new TableSplit(tableName, startRowKey, endRowKey, rl.toString());

            splits.add(split);
        }

        return splits;
    }
}
