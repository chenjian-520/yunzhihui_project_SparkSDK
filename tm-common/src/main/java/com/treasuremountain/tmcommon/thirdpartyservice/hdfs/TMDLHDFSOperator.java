package com.treasuremountain.tmcommon.thirdpartyservice.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TMDLHDFSOperator {

    private static Configuration conf = null;
    private static String currentHDFSPath = null;

    public static synchronized void init(String hdfspath) {
        conf = new Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");
        conf.setBoolean("dfs.support.append", true);
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        currentHDFSPath = hdfspath;
    }

    private static FileSystem getFileSystem(String user) throws URISyntaxException, IOException, InterruptedException {
        return FileSystem.get(new URI(currentHDFSPath), conf, user);
    }

    public static void baseFileUpload(String filePath, String action, InputStream in, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        if (action.equals("create")) {
            FSDataOutputStream outputStream = fileSystem.create(new Path(filePath));
            IOUtils.copyBytes(in, outputStream, 4096, true);
        } else if (action.equals("append")) {
            FSDataOutputStream out = fileSystem.append(new Path(filePath));
            IOUtils.copyBytes(in, out, 4096, true);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void baseFileDownLoad(String filePath, OutputStream out, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        InputStream is = fileSystem.open(new Path(filePath));
        IOUtils.copyBytes(is, out, 4096, true);
    }

    public static int getFileLength(String filePath, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        InputStream is = fileSystem.open(new Path(filePath));
        byte[] bytes = new byte[is.available()];
        return bytes.length;
    }

    public static void baseDeleteFile(String filePath, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        fileSystem.delete(new Path(filePath), false);
    }

    public static List<TMDLHDFSFilePath> baseFileList(String filePath, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        FileStatus[] fileStatuses = fileSystem.listStatus(new Path(filePath));

        List<TMDLHDFSFilePath> tmpathList = new ArrayList<>();

        for (FileStatus fileStatus : fileStatuses) {

            String type = fileStatus.isDirectory() ? "folder" : "file";

            String path = fileStatus.getPath().toString();
            String[] paths = path.split("/");

            int pathLength = paths.length;
            String realpath = "/";

            for (int i = 5; i < pathLength; i++) {
                realpath += paths[i];
                if (i + 1 < pathLength) {
                    realpath += "/";
                }
            }

            TMDLHDFSFilePath tmpath = new TMDLHDFSFilePath();
            tmpath.setPath(realpath);
            tmpath.setType(type);

            tmpathList.add(tmpath);
        }

        return tmpathList;
    }

    public static String getFileName(String filePath) {
        return new Path(filePath).getName();
    }

    public static Boolean mkDir(String filePath, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        return fileSystem.mkdirs(new Path(filePath));
    }

    public static Boolean rmDir(String filePath, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        return fileSystem.delete(new Path(filePath), true);
    }

    public static Boolean rename(String srcPath, String dstPath, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        return fileSystem.rename(new Path(srcPath), new Path(dstPath));
    }

    public static FileStatus[] listFileStatus(String path, String user) throws Exception {
        FileSystem fileSystem = getFileSystem(user);
        return fileSystem.listStatus(new Path(path));
    }
}
