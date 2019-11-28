package com.iisd.dpsparktest;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.hbase.DPHbase;
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
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class readhbase extends DPSparkBase implements Serializable {
    public void main(Map<String, String> map) throws Exception {

        Scan scan = new Scan();

//        FilterList filterList = new FilterList();
//        filterList.addFilter(new PageFilter(2));
//
//        scan.setFilter(filterList);

//        scan.withStartRow(Bytes.toBytes("1558058400000"));
//        scan.withStopRow(Bytes.toBytes("1558062000000"));

        JavaPairRDD<ImmutableBytesWritable, Result> myRDD = DPHbase.rddRead("sys_group_user20190710102823", scan);

        JavaRDD<Row> personsRDD = myRDD.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Row>() {

            public Row call(Tuple2<ImmutableBytesWritable, Result> tuple) throws Exception {

                Result result = tuple._2();
                String rowkey = Bytes.toString(result.getRow());

                String bu = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("bu")));
                String custpn = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("custpn")));
                String filename = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("filename")));
                String hhpn = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("hhpn")));
                String scantime_stamp = Bytes.toString(result.getValue(Bytes.toBytes("mfg_assembly_detail"), Bytes.toBytes("scantime_stamp")));
                long ltmp = Long.parseLong(scantime_stamp);

                return (Row) RowFactory.create(rowkey, bu, custpn, filename, hhpn, ltmp);
            }

        });


        List<StructField> structFields = new ArrayList<StructField>();
        structFields.add(DataTypes.createStructField("id", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("bu", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("custpn", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("filename", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("hhpn", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("scantime_stamp", DataTypes.LongType, true));

        StructType schema = DataTypes.createStructType(structFields);

        SparkSession spark = DPSparkApp.getSession();

        Dataset stuDf = spark.createDataFrame(personsRDD, schema);
        //stuDf.select("id","name","age").write().mode(SaveMode.Append).parquet("par");
        stuDf.printSchema();
        stuDf.createOrReplaceTempView("sys_group_user20190710102823");

//            Dataset<Row> nameDf = spark.sql("select * from mfg_assembly_detail order by scantime_stamp desc LIMIT 20");
        Dataset<Row> nameDf = spark.sql("select count(*) from sys_group_user20190710102823 where scantime_stamp >= 1558058400000 and scantime_stamp < 1558062000000");
        nameDf.show();

        DPSparkApp.stop();
    }
}
