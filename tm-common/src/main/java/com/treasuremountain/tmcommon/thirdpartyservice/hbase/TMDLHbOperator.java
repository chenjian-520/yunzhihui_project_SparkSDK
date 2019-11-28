package com.treasuremountain.tmcommon.thirdpartyservice.hbase;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.message.*;
import com.treasuremountain.tmcommon.thirdpartyservice.tools.MD5Helper;
import lombok.Synchronized;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.security.UserGroupInformation;

import javax.security.auth.Subject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by gerryzhao on 10/16/2018.
 */
public class TMDLHbOperator {

    private static Configuration conf;
    private static final Cache<String, Connection> connectionPool = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite(5L, TimeUnit.MINUTES).build();

    private synchronized static Connection createConnection(String user) throws IOException {
        System.getProperties().setProperty("HADOOP_USER_NAME", user);
        UserGroupInformation.loginUserFromSubject((Subject) null);
        return ConnectionFactory.createConnection(conf);
    }

    public static Configuration init(String quorum, String clientPort) throws IOException {
        //初始化存入默认用户hadoop的连接池信息
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", quorum);
        conf.set("hbase.zookeeper.clientPort", clientPort);
        conf.set("hbase.client.operation.timeout", "60000");
        conf.set("hbase.rpc.timeout", "60000");
        Connection connection = createConnection("hadoop");
        connectionPool.put("hadoop", connection);
        return conf;
    }

    private static Connection getCurrentConnection(String user) throws IOException {
        if (connectionPool.getIfPresent(user) == null) {
            return createConnection(user);
        } else {
            return connectionPool.getIfPresent(user);
        }
    }

//    public static void releaseConnection() {
//        if (connection != null) {
//            try {
//                connection.close();
//            } catch (IOException e) {
//                //ignore
//            }
//        }
//    }

    public static void createNamespace(String namespace, String user) throws IOException {
        Connection connection = getCurrentConnection(user);
        if (!isExistsNamespace(namespace, user)) {
            connection.getAdmin().createNamespace(NamespaceDescriptor.create("namespace").build());
        }
    }

    public static void createTable(String tableNameString, String[] columnFamilies, byte[][] splitKeys, String compressionType, String user) throws IOException {
        Connection connection = getCurrentConnection(user);
        HbaseCommon.createTable(connection.getAdmin(), tableNameString, columnFamilies, splitKeys, compressionType);
    }

    public static HbaseTableMsg queryTableBatch(String tableName, List<HbaseQueryColumnMsg> columns,
                                                List<String> rowkeyList, String user) throws IOException {
        Connection connection = getCurrentConnection(user);
        Table table = connection.getTable(TableName.valueOf(tableName));

        List<Get> getList = new ArrayList();

        for (String rowkey : rowkeyList) {
            Get get = new Get(Bytes.toBytes(rowkey));
            getList.add(get);
        }

        HbaseTableMsg hbaseTableMsg = new HbaseTableMsg();
        hbaseTableMsg.setTableName(tableName);
        hbaseTableMsg.setShowCount(0);
        hbaseTableMsg.setIsNextPage("not support");

        List<HbaseRowEntity> hbaseRowEntities = new ArrayList<>();

        Result[] results = table.get(getList);

        for (Result result : results) {
            if (result.getRow() != null) {
                hbaseRowEntities.add(makehbaseRowEntity(false, result, columns));
            }
        }

        hbaseTableMsg.setRowList(hbaseRowEntities);

        return hbaseTableMsg;
    }

    public static HbaseTableMsg queryTable(String tableName, List<HbaseQueryColumnMsg> columns,
                                           String startRowKey, String endRowKey, long showCount, String user) throws IOException {
        Connection connection = getCurrentConnection(user);
        Table table = connection.getTable(TableName.valueOf(tableName));

        Scan scan = new Scan();

        scan.withStartRow(Bytes.toBytes(startRowKey));
        scan.withStopRow(Bytes.toBytes(endRowKey));

        if (showCount > 0) {
            FilterList filterList = new FilterList();
            filterList.addFilter(new PageFilter(showCount));

            scan.setFilter(filterList);
        }

        ResultScanner resultScanner = table.getScanner(scan);

        HbaseTableMsg hbaseTableMsg = new HbaseTableMsg();
        hbaseTableMsg.setTableName(tableName);
        hbaseTableMsg.setShowCount(showCount);

        List<HbaseRowEntity> hbaseRowEntities = new ArrayList<>();

        for (Result result : resultScanner) {
            hbaseRowEntities.add(makehbaseRowEntity(false, result, columns));
        }

        hbaseTableMsg.setIsNextPage("not support");
        hbaseTableMsg.setRowList(hbaseRowEntities);

        return hbaseTableMsg;
    }

    public static HbaseTableMsg querySaltTable(String tableName, List<HbaseQueryColumnMsg> columns,
                                               String startRowKey, String endRowKey, long showCount, String user) throws IOException {
        Connection connection = getCurrentConnection(user);
        Table table = connection.getTable(TableName.valueOf(tableName));

        Scan scan = new Scan();

        if (showCount > 0) {
            FilterList filterList = new FilterList();
            filterList.addFilter(new PageFilter(showCount));

            scan.setFilter(filterList);
        }

        TableName otableName = TableName.valueOf(tableName);
        RegionLocator regionLocator = connection.getRegionLocator(otableName);
        Pair<byte[][], byte[][]> keys = regionLocator.getStartEndKeys();

        List<HbaseRowEntity> hbaseRowEntities = new ArrayList<>();

        for (int i = 0; i < keys.getFirst().length; i++) {
            byte[] b = keys.getFirst()[i];

            String regionSalt = null;
            if (b.length > 0) {
                regionSalt = Bytes.toString(b);
            } else {
                regionSalt = "00";
            }

            String s = regionSalt + ":" + startRowKey;
            String e = regionSalt + ":" + endRowKey;

            scan.withStartRow(Bytes.toBytes(s));
            scan.withStopRow(Bytes.toBytes(e));

            ResultScanner resultScanner = table.getScanner(scan);

            for (Result result : resultScanner) {
                hbaseRowEntities.add(makehbaseRowEntity(true, result, columns));
            }
        }

        List<HbaseRowEntity> result = hbaseRowEntities.stream().parallel().sorted(Comparator.comparing(HbaseRowEntity::getRowKey)).collect(Collectors.toList());

        HbaseTableMsg hbaseTableMsg = new HbaseTableMsg();

        hbaseTableMsg.setTableName(tableName);
        hbaseTableMsg.setShowCount(showCount);
        hbaseTableMsg.setRowList(result);
        hbaseTableMsg.setIsNextPage("not support");

        return hbaseTableMsg;
    }

    public static HbaseTableMsg queryTableFilter(String tableName, List<HbaseQueryColumnMsg> columns, String startRowKey,
                                                 String endRowKey, List<HbaseFilterParmMsg> filterParmMsgs, long showCount, String user) throws IOException {
        Connection connection = getCurrentConnection(user);
        Table table = connection.getTable(TableName.valueOf(tableName));

        Scan scan = new Scan();

        scan.withStartRow(Bytes.toBytes(startRowKey));
        scan.withStopRow(Bytes.toBytes(endRowKey));

        scan.setFilter(makeFilterList(filterParmMsgs, showCount));

        ResultScanner resultScanner = table.getScanner(scan);

        HbaseTableMsg hbaseTableMsg = new HbaseTableMsg();
        hbaseTableMsg.setTableName(tableName);
        hbaseTableMsg.setShowCount(showCount);

        List<HbaseRowEntity> hbaseRowEntities = new ArrayList<>();

        for (Result result : resultScanner) {
            hbaseRowEntities.add(makehbaseRowEntity(false, result, columns));
        }

        hbaseTableMsg.setRowList(hbaseRowEntities);
        hbaseTableMsg.setIsNextPage("not support");

        return hbaseTableMsg;
    }

    public static HbaseTableMsg querySaltTableFilter(int searchThreadCount, String tableName, List<HbaseQueryColumnMsg> columns, String startRowKey, String endRowKey, List<HbaseFilterParmMsg> filterParmMsgs, long showCount, String user) throws IOException, ExecutionException, InterruptedException {
        Connection connection = getCurrentConnection(user);
        Table table = connection.getTable(TableName.valueOf(tableName));

        TableName otableName = TableName.valueOf(tableName);
        RegionLocator regionLocator = connection.getRegionLocator(otableName);
        Pair<byte[][], byte[][]> keys = regionLocator.getStartEndKeys();

        List<HbaseRowEntity> hbaseRowEntities = new ArrayList<>();

        List<byte[]> saltList = new ArrayList<>();

        for (int i = 0; i < keys.getFirst().length; i++) {
            byte[] b = keys.getFirst()[i];
            saltList.add(b);
        }

        new ForkJoinPool(searchThreadCount).submit(() -> {

            saltList.parallelStream().forEach(b -> {

                try {

                    Scan scan = new Scan();

                    String regionSalt = null;
                    if (b.length > 0) {
                        regionSalt = Bytes.toString(b);
                    } else {
                        regionSalt = "00";
                    }

                    String s = regionSalt + ":" + startRowKey;
                    String e = regionSalt + ":" + endRowKey;

                    scan.withStartRow(Bytes.toBytes(s));
                    scan.withStopRow(Bytes.toBytes(e));

                    scan.setFilter(makeFilterList(filterParmMsgs, showCount));

                    ResultScanner resultScanner = table.getScanner(scan);

                    for (Result result : resultScanner) {
                        hbaseRowEntities.add(makehbaseRowEntity(true, result, columns));
                    }
                } catch (Exception ex) {
                    hbaseRowEntities.add(new HbaseRowEntity());
                }
            });

        }).get();

//        for (int i = 0; i < keys.getFirst().length; i++) {

//            Scan scan = new Scan();

//            byte[] b = keys.getFirst()[i];
//
//            String regionSalt = null;
//            if (b.length > 0) {
//                regionSalt = Bytes.toString(b);
//            } else {
//                regionSalt = "00";
//            }
//
//            String s = regionSalt + ":" + startRowKey;
//            String e = regionSalt + ":" + endRowKey;
//
//            scan.withStartRow(Bytes.toBytes(s));
//            scan.withStopRow(Bytes.toBytes(e));
//
//            scan.setFilter(makeFilterList(filterParmMsgs, showCount));
//
//            ResultScanner resultScanner = table.getScanner(scan);
//
//            for (Result result : resultScanner) {
//                hbaseRowEntities.add(makehbaseRowEntity(true, result, columns));
//            }
//        }

        List<HbaseRowEntity> result = hbaseRowEntities.stream().parallel().sorted(Comparator.comparing(HbaseRowEntity::getRowKey)).collect(Collectors.toList());

        HbaseTableMsg hbaseTableMsg = new HbaseTableMsg();

        hbaseTableMsg.setTableName(tableName);
        hbaseTableMsg.setShowCount(showCount);
        hbaseTableMsg.setRowList(result);
        hbaseTableMsg.setIsNextPage("not support");

        return hbaseTableMsg;
    }

    public static void insert(String tableName, List<HbaseRowEntity> rowList, String user) throws IOException {
        Connection connection = getCurrentConnection(user);
        Table table = connection.getTable(TableName.valueOf(tableName));
        List<Put> putList = new ArrayList<Put>();
        rowList.forEach(d -> {

            String rowKey = d.getRowKey();
            Put put = new Put(Bytes.toBytes(rowKey));

            List<HbaseColumnFamilyEntity> columnFamilyList = d.getColumnFamily();
            columnFamilyList.forEach(e -> {
                String familyName = e.getColumnFamilyName();
                List<HbaseColumnEntity> columnFamilyValue = e.getColumnList();
                columnFamilyValue.forEach(f -> {
                    String columnName = f.getColumnName();
                    String value = f.getColumnValue();
                    if (columnName != null && value != null) {
                        put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName), Bytes.toBytes(value));
                    }
                });
            });

            putList.add(put);
        });

        table.put(putList);
    }

    private static FilterList makeFilterList(List<HbaseFilterParmMsg> filterParmMsgs, long showCount) {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);

        filterParmMsgs.forEach(d -> {
            switch (d.getType()) {
                case STRING:
                    SingleColumnValueFilter vfilter = new SingleColumnValueFilter(
                            Bytes.toBytes(d.getColumnFamily()),
                            Bytes.toBytes(d.getColumn()),
                            CompareOperator.EQUAL,
                            Bytes.toBytes(d.getValue())
                    );
                    filterList.addFilter(vfilter);
                    break;
                case REGEX:
                    SingleColumnValueFilter rfilter = new SingleColumnValueFilter(
                            Bytes.toBytes(d.getColumnFamily()),
                            Bytes.toBytes(d.getColumn()),
                            CompareOperator.EQUAL,
                            new RegexStringComparator(d.getValue())
                    );
                    filterList.addFilter(rfilter);
                    break;
            }
        });

        if (showCount > 0) {
            Filter filter = new PageFilter(showCount);
            filterList.addFilter(filter);
        }

        return filterList;
    }

    private static boolean isExistsNamespace(String strNamespace, String user) throws IOException {
        Connection connection = getCurrentConnection(user);
        NamespaceDescriptor[] namespaces = connection.getAdmin().listNamespaceDescriptors();
        for (int i = 0; i < namespaces.length; i++) {
            if (strNamespace.equals(namespaces[i].getName())) {
                return true;
            }
        }
        return false;
    }

    private static HbaseRowEntity makehbaseRowEntity(boolean isSalt, Result result, List<HbaseQueryColumnMsg> columns) {

        byte[] row = result.getRow();

        String rowkey = Bytes.toString(row);

        HbaseRowEntity hbaseRowEntity = new HbaseRowEntity();

        if (isSalt) {
            hbaseRowEntity.setSalt(rowkey.substring(0, 3));
            rowkey = rowkey.substring(3, rowkey.length());
        }

        hbaseRowEntity.setRowKey(rowkey);

        List<HbaseColumnFamilyEntity> columnFamilyEntities = new ArrayList<>();

        for (Cell cell : result.rawCells()) {

            String columnFamilyName = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
            String columnName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            String columnValue = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());

            boolean isReturn = false;

            if (columns != null && columns.size() > 0) {

                Optional<HbaseQueryColumnMsg> ohqc = columns.stream().filter(d -> d.getColumnFamily().equals(columnFamilyName)
                        && d.getColumn().equals(columnName)).findFirst();

                if (ohqc.isPresent()) {
                    isReturn = true;
                }
            } else {
                isReturn = true;
            }

            if (isReturn) {
                Optional<HbaseColumnFamilyEntity> hcfe = columnFamilyEntities.stream().filter(d -> d.getColumnFamilyName()
                        .equals(columnFamilyName)).findFirst();

                HbaseColumnFamilyEntity rhcfe;

                if (hcfe.isPresent()) {
                    rhcfe = hcfe.get();

                    HbaseColumnEntity hfve = new HbaseColumnEntity();
                    hfve.setColumnName(columnName);
                    hfve.setColumnValue(columnValue);

                    rhcfe.getColumnList().add(hfve);

                } else {
                    rhcfe = new HbaseColumnFamilyEntity();
                    rhcfe.setColumnFamilyName(columnFamilyName);
                    List<HbaseColumnEntity> newColumnList = new ArrayList<>();
                    rhcfe.setColumnList(newColumnList);

                    HbaseColumnEntity hfve = new HbaseColumnEntity();
                    hfve.setColumnName(columnName);
                    hfve.setColumnValue(columnValue);

                    rhcfe.getColumnList().add(hfve);

                    columnFamilyEntities.add(rhcfe);
                }

                hbaseRowEntity.setColumnFamily(columnFamilyEntities);
            }

        }

        return hbaseRowEntity;
    }

    public static String makeCommonRowKey(String timeSpan, int maxRegion) {
        String s = UUID.randomUUID().toString().replace("-", "");
        s = MD5Helper.encrypt16(s);

        Random ra = new Random();

        int ci = ra.nextInt(maxRegion + 1);

        String rs = Integer.toString(ci);
        if (ci < 10) {
            rs = "0" + ci;
        }

        return rs + ":" + timeSpan + ":" + s;
    }

    //Disable表
    public static void disableTable(String tableName, String user) throws Exception {
        Connection connection = getCurrentConnection(user);
        HbaseCommon.disableTable(connection.getAdmin(), tableName);
    }


}
