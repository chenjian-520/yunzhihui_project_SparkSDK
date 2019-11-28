package com.tm.dl.javasdk.dpspark.hdfs;

import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.common.PermissionManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DPHdfs {


    static FileSystem fileSystem = null;

    public static JavaRDD<String> readTextFile(String path, int partitionCount) {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        JavaSparkContext sparkContext = DPSparkApp.getContext();
        String realPath = pm.getRootHdfsUri() + path;
        JavaRDD<String> txtData = sparkContext.textFile(realPath, partitionCount).cache();
        return txtData;
    }

    public static boolean deleteFile(String path) throws IOException {
        return fileSystem.delete(new Path(path), false);
    }

    public static void initHDFSFileSystem() throws URISyntaxException, IOException, InterruptedException {
        PermissionManager pm = DPSparkApp.getDpPermissionManager();
        String user = pm.getUserPermission(DPSparkApp.getDpuserid());
        fileSystem = FileSystem.get(new URI(pm.getRootHdfsUri()), pm.initialHdfsSecurityContext(), user);
    }
}
