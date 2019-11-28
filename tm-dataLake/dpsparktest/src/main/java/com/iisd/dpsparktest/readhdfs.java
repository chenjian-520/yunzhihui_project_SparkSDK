package com.iisd.dpsparktest;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.hdfs.DPHdfs;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;
import java.util.Map;

public class readhdfs extends DPSparkBase implements Serializable {
    public void main(Map<String, String> map) throws Exception {

        DPHdfs.initHDFSFileSystem();

        JavaRDD<String> logData = DPHdfs.readTextFile("/dptemp/gerrytest/file/*", 5);

        long numAs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) { return s.contains("a"); }
        }).count();

        long numBs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) { return s.contains("b"); }
        }).count();

        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);

        DPSparkApp.stop();
    }
}
