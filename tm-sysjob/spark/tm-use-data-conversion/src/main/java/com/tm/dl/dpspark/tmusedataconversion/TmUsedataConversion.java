package com.tm.dl.dpspark.tmusedataconversion;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.hbase.DPHbase;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.RowFactory;
import scala.Tuple2;

/**
 * Description:
 * <p>从旧的tm_useraction_log 表中读取数据然后存入对应的新表中
 * Created by ref.tian on 2019/5/23.
 * Company: Foxconn
 * Project: TreasureMountain
 */
public class TmUsedataConversion extends DPSparkBase implements Serializable {

    public void main(Map<String, String> map) throws Exception {

        Scan scan = new Scan();

        JavaPairRDD<ImmutableBytesWritable, Result> useractionLog = DPHbase.rddRead("tm_useraction_log", scan);
        System.out.println("数据数量："+useractionLog.count());
        JavaRDD<Row> useractionlogRddRow = useractionLog.map(new Function<Tuple2<ImmutableBytesWritable, Result>, Row>() {
            @Override
            public Row call(Tuple2<ImmutableBytesWritable, Result> tuple2) throws Exception {
                Result resulto = tuple2._2();

                String browsertype = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("browsertype")));
                String browserversion = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("browserversion")));
                String clientip = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("clientip")));
                String filename = Bytes.toString(resulto.getValue(Bytes.toBytes("infoext1"), Bytes.toBytes("filename")));
                String lang = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("lang")));
                String menuid = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("menuid")));
                String menuname = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("menuname")));
                String result = Bytes.toString(resulto.getValue(Bytes.toBytes("infoext1"), Bytes.toBytes("result")));
                String sessionid = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("sessionid")));
                String sysname = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("sysname")));
                String systimestamp = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("systimestamp")));
                String terminaltype = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("terminaltype")));
                String timestamp = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("timestamp")));
                String type = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("type")));
                String usercode = Bytes.toString(resulto.getValue(Bytes.toBytes("info"), Bytes.toBytes("usercode")));

                Integer i = RandomUtils.nextInt(0, 10);
                String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
                String rowkey = String.format("%02d", i) + ":" + systimestamp + ":INSIGHT:" + usercode + ":" + type + ":" + uuid;

                return (Row) RowFactory.create(rowkey, browsertype, browserversion, clientip, filename, lang, menuid, menuname, result,
                        sessionid, sysname, systimestamp, terminaltype, timestamp, type, usercode
                );
            }
        });
        JavaPairRDD<ImmutableBytesWritable, Put> useractionRddPut = useractionlogRddRow.mapToPair(new PairFunction<Row, ImmutableBytesWritable, Put>() {
            @Override
            public Tuple2<ImmutableBytesWritable, Put> call(Row row) throws Exception {

                String rowkey = row.get(0).toString();
                String browsertype =row.get(1)!=null ?row.get(1).toString():"";
                String browserversion = row.get(2)!=null ?row.get(2).toString():"";
                String clientip = row.get(3)!=null ? row.get(3).toString():"";
                String filename = row.get(4)!=null?row.get(4).toString():"";
                String lang = row.get(5)!=null?row.get(5).toString():"";
                String menuid = row.get(6)!=null?row.get(6).toString():"";
                String menuname = row.get(7)!=null?row.get(7).toString():"";
                String result = row.get(8)!=null?row.get(8).toString():"";
                String sessionid = row.get(9)!=null?row.get(9).toString():"";
                String sysname = row.get(10)!=null?row.get(10).toString():"";
                String systimestamp =row.get(11)!=null? row.get(11).toString():"";
                String terminaltype =row.get(12)!=null?row.get(12).toString():"" ;
                String timestamp = row.get(13)!=null?row.get(13).toString():"";
                String type = row.get(14)!=null ? row.get(14).toString():"";
                String usercode =row.get(15)!=null? row.get(15).toString():"";

                Put put = new Put(Bytes.toBytes(rowkey));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("browsertype"), Bytes.toBytes(browsertype));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("browserversion"), Bytes.toBytes(browserversion));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("clientip"), Bytes.toBytes(clientip));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("lang"), Bytes.toBytes(lang));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("menuid"), Bytes.toBytes(menuid));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("menuname"), Bytes.toBytes(menuname));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("sessionid"), Bytes.toBytes(sessionid));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("sysname"), Bytes.toBytes(sysname));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("systimestamp"), Bytes.toBytes(systimestamp));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("terminaltype"), Bytes.toBytes(terminaltype));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("timestamp"), Bytes.toBytes(timestamp));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("type"), Bytes.toBytes(type));
                put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("usercode"), Bytes.toBytes(usercode));

                put.addColumn(Bytes.toBytes("infoext1"), Bytes.toBytes("filename"), Bytes.toBytes(filename));
                put.addColumn(Bytes.toBytes("infoext1"), Bytes.toBytes("result"), Bytes.toBytes(result));

                return new Tuple2<ImmutableBytesWritable, Put>(new ImmutableBytesWritable(), put);
            }
        });

        DPHbase.rddWrite("tm_useraction_log_20190523085246", useractionRddPut);

        DPSparkApp.stop();
    }
}
