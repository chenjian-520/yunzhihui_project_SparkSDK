package com.treasuremountain.tmcommon.thirdpartyservice.hbase;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HbaseCommon {
    public static void createTable(Admin admin, String tableNameString, String[] columnFamilies, byte[][] splitKeys, String compressionType) throws IOException {

        TableName tableName = TableName.valueOf(tableNameString);

        if (!admin.tableExists(tableName)) {
            TableDescriptorBuilder tableDescriptor = TableDescriptorBuilder.newBuilder(tableName);

            for (int i = 0; i < columnFamilies.length; i++) {

                ColumnFamilyDescriptorBuilder builder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamilies[i]));

                if (StringUtils.isNotBlank(compressionType)) {
                    builder.setCompressionType(Compression.Algorithm.valueOf(compressionType));
                }
                builder.setScope(1);

                ColumnFamilyDescriptor columnDescriptor = builder.build();

                tableDescriptor.setColumnFamily(columnDescriptor);

            }

            if (splitKeys.length > 0) {
                admin.createTable(tableDescriptor.build(), splitKeys);
            } else {
                admin.createTable(tableDescriptor.build());
            }
        } else {
            throw new UnsupportedOperationException("table exists");
        }
    }

    //Disable表
    public static void disableTable(Admin admin, String tableNameString) throws Exception {
        TableName tableName = TableName.valueOf(tableNameString);
        if (admin.tableExists(tableName)) {
            admin.disableTable(tableName);
        } else {
            throw new UnsupportedOperationException("table not exists");
        }
    }
}
