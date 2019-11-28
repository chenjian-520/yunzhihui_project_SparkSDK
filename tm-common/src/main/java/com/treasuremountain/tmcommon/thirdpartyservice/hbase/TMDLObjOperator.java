package com.treasuremountain.tmcommon.thirdpartyservice.hbase;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class TMDLObjOperator {

    private Connection connection;
    private Admin admin;

    public TMDLObjOperator(String quorum, String clientPort) throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", quorum);
        config.set("hbase.zookeeper.clientPort", clientPort);
        config.set("hbase.client.operation.timeout", "60000");
        config.set("hbase.rpc.timeout", "60000");
        this.connection = ConnectionFactory.createConnection(config);
        this.admin = connection.getAdmin();
    }

    public void createTable(String tableNameString, String[] columnFamilies, byte[][] splitKeys, String compressionType) throws IOException {
        HbaseCommon.createTable(this.admin, tableNameString, columnFamilies, splitKeys, compressionType);
        connection.close();
    }

}
