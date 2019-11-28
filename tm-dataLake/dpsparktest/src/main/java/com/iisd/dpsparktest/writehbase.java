package com.iisd.dpsparktest;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.hbase.DPHbase;
import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLHbOperator;
import com.treasuremountain.tmcommon.thirdpartyservice.tools.MD5Helper;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

public class writehbase extends DPSparkBase implements Serializable {

    public void main(Map<String, String> map) throws Exception {

        Scan scan = new Scan();

//        FilterList filterList = new FilterList();
//        filterList.addFilter(new PageFilter(2));
//
//        scan.setFilter(filterList);

        JavaPairRDD<ImmutableBytesWritable, Result> myRDD = DPHbase.rddRead("mfg_assembly_detail", scan);

        JavaRDD<Row> personsRDD = myRDD.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Row>() {

            public Row call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {
                Result result = tuple._2();
                String rowkey = Bytes.toString(result.getRow());

                String bu = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("bu")));
                String custpn = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("custpn")));
                String emp_id = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("emp_id")));
                String filename = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("filename")));
                String hhpn = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("hhpn")));
                String id = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("id")));
                String is_disassembly = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("is_disassembly")));
                String keypart_sn = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("keypart_sn")));
                String line_id = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("line_id")));
                String part_name = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("part_name")));
                String scantime = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("scantime")));
                String scantime_stamp = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("scantime_stamp")));
                String station_name = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("station_name")));

                return (Row) RowFactory.create(rowkey, bu, custpn, emp_id, filename, hhpn, id, is_disassembly, keypart_sn, line_id, part_name, scantime, scantime_stamp, station_name);
            }

        });

        JavaPairRDD<ImmutableBytesWritable, Put> hbasePuts = personsRDD.mapToPair(
                new PairFunction<Row, ImmutableBytesWritable, Put>() {
                    public Tuple2<ImmutableBytesWritable, Put> call(Row r) throws Exception {

                        String rowkey = r.get(0).toString();
                        String bu = r.get(1).toString();
                        String custpn = r.get(2).toString();
                        String emp_id = r.get(3).toString();
                        String filename = r.get(4).toString();
                        String hhpn = r.get(5).toString();
                        String id = r.get(6).toString();
                        String is_disassembly = r.get(7).toString();
                        String keypart_sn = r.get(8).toString();
                        String line_id = r.get(9).toString();
                        String part_name = r.get(10).toString();
                        String scantime = r.get(11).toString();
                        String scantime_stamp = r.get(12).toString();
                        String station_name = r.get(13).toString();

                        String s = UUID.randomUUID().toString().replace("-", "");
                        s = MD5Helper.encrypt16(s);

                        Random ra = new Random();

                        int ci = ra.nextInt(20);

                        String rs = Integer.toString(ci);
                        if (ci < 10) {
                            rs = "0" + ci;
                        }

                        rowkey = rs + ":" + scantime_stamp + ":" + bu + ":" + s;

                        Put put = new Put(Bytes.toBytes(rowkey));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("bu"), Bytes.toBytes(bu));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("custpn"), Bytes.toBytes(custpn));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("emp_id"), Bytes.toBytes(emp_id));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("filename"), Bytes.toBytes(filename));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("hhpn"), Bytes.toBytes(hhpn));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("id"), Bytes.toBytes(id));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("is_disassembly"), Bytes.toBytes(is_disassembly));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("keypart_sn"), Bytes.toBytes(keypart_sn));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("line_id"), Bytes.toBytes(line_id));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("part_name"), Bytes.toBytes(part_name));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("scantime"), Bytes.toBytes(scantime));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("scantime_stamp"), Bytes.toBytes(scantime_stamp));
                        put.addColumn(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("station_name"), Bytes.toBytes(station_name));


                        return new Tuple2<ImmutableBytesWritable, Put>(new ImmutableBytesWritable(), put);
                    }
                });

        DPHbase.rddWrite("mfg_assembly_detail20190521103901", hbasePuts);

        DPSparkApp.stop();
    }
}
