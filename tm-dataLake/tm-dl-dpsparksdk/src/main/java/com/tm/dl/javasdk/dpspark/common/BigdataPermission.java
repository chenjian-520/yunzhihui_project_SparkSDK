package com.tm.dl.javasdk.dpspark.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.TMHttpClient;
import com.treasuremountain.tmcommon.thirdpartyservice.httpclient.message.MTHttpCallbackParms;
import org.apache.http.nio.reactor.IOReactorException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/4/16.
 * Company: Foxconn
 * Project: MaxIoT
 */
/**
 * @deprecated 功能合并到 ProdPermissionManager
 * */
public class BigdataPermission {

    public BigdataPermission() {

    }

    private static class BigdataPermissionInstance {
        public static final BigdataPermission INSTANCE = new BigdataPermission();
    }

    public static final BigdataPermission getInstance() {
        return BigdataPermissionInstance.INSTANCE;
    }

    private static String managementpermissionurl = "http://tmmanagement:9102/api/common/v1/user/bigdata/permission/";
    private ObjectMapper om = new ObjectMapper();

    /**
     * 初始化，获取用户权限信息
     *
     * @param dpuserid 用户id
     **/
    public String init(String dpuserid) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final String[] restulstr = new String[1];
        try{
            getBigdataPermission(dpuserid, countDownLatch, new Consumer<String>() {
                @Override
                public void accept(String s) {

                    restulstr[0] = s;
                    countDownLatch.countDown();
                    return;
                }
            });
            countDownLatch.await();
        }catch (Exception e){
            countDownLatch.countDown();
            return "";
        }
        return restulstr[0];
    }

    /**
     * @param permissionstring 需要判断的用户id,不能为空
     * @param htable           hbase表名
     * @param permissioncode   权限code ，all（全部）/read（读）/write（写）/delete（删除）/read_write(读写)
     * @return 返回boolean ，有权限（true）
     ***/
    public boolean haveHbaseTablePermission(String permissionstring, String htable, String permissioncode) throws IOException {
        boolean result = false;
        if (!permissionstring.isEmpty()) {
            JsonNode jsonNode = null;
            jsonNode = om.readTree(permissionstring);
            if (jsonNode == null || jsonNode.get("hbaseDatagroupDtos").size() == 0) {
                return false;
            } else {
                for (Iterator<JsonNode> it = jsonNode.get("hbaseDatagroupDtos").elements(); it.hasNext(); ) {
                    JsonNode jn = it.next();
                    if (jn.get("hbaseTableName").asText().equals(htable)) {
                        if (jn.get("permissionCode").asText().equals("all")) {
                            return true;
                        } else {
                            String[] parray = permissioncode.split("_");
                            String[] dparray = jn.get("permissionCode").asText().split("_");
                            result = findPermissionCode(parray, dparray);
                            return result;
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * @param permissionstring       需要判断的用户id
     * @param hdfspath       hdfs目录名
     * @param permissioncode 权限code ，all（全部）/read（读）/write（写）/delete（删除）/read_write(读写)
     * @return 返回boolean ，有权限（true）
     ***/
    public boolean haveHdfsPathPermission(String permissionstring, String hdfspath, String permissioncode) throws IOException {
        if (!permissionstring.isEmpty()) {
            JsonNode jsonNode = null;
                jsonNode = om.readTree(permissionstring);
                if (jsonNode == null || jsonNode.get("hdfsDatagroupDtos").size() == 0) {
                    return false;
                } else {
                    Map<String, HdfsFolder> dbFolderPermission = new LinkedHashMap<>();
                    // 处理数据库中设置的文件夹权限信息
                    CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
                    for (Iterator<JsonNode> it = jsonNode.get("hdfsDatagroupDtos").elements(); it.hasNext(); ) {
                        JsonNode jn = it.next();
                        String path1 = jn.get("hdfsFolderPath").asText();
                        if (path1 != null) {
                            String[] pathArray = path1.split("/");
                            copyOnWriteArrayList.clear();
                            copyOnWriteArrayList.addAll(0, Arrays.asList(pathArray));
                            copyOnWriteArrayList.remove(0);
                            findfolder(copyOnWriteArrayList, dbFolderPermission, jn.get("permissionCode").asText(), null);
//                            System.out.println("--------------------");
                        }
                    }
                    copyOnWriteArrayList.clear();
                    String[] hdfsarray = hdfspath.split("/");
                    copyOnWriteArrayList.addAll(Arrays.asList(hdfsarray));
                    copyOnWriteArrayList.remove(0);
                    HdfsFolder hdfsFolder = dbFolderPermission.get(copyOnWriteArrayList.get(0));
                    if (hdfsFolder == null) {
                        return false;
                    } else {
                        copyOnWriteArrayList.remove(0);
                        return findHdfsPermission(hdfsFolder, copyOnWriteArrayList, permissioncode);
                    }
                }
        } else {
            return false;
        }
    }

    /**
     * 发送http请求
     *
     * @param dpuserid       账号id
     * @param countDownLatch 线程挂起标识
     * @param consumer       回调
     **/
    private void getBigdataPermission(String dpuserid, CountDownLatch countDownLatch, Consumer<String> consumer) {
        try {
            TMHttpClient.init();
            HashMap<String, String> header = new HashMap<>();
            header.putIfAbsent("Content-Type", "application/json");
            TMHttpClient.doGet(managementpermissionurl + dpuserid, header, new Consumer<MTHttpCallbackParms>() {
                @Override
                public void accept(MTHttpCallbackParms o) {
                    if (o.getStatusCode() == 200) {
//                        System.out.print(o.getResponseBody());
                        try {
                            JsonNode jsonNode = om.readTree(o.getResponseBody());
                            JsonNode datanode = jsonNode.get("data");
                            if (datanode == null || datanode.size() == 0) {
                                consumer.accept("");
                            } else {
                                consumer.accept(datanode.toString());
                            }
                        } catch (IOException e) {
                            consumer.accept("");
                            return;
                        }
                    } else {
                        consumer.accept("");
                        return;
                    }
                }
            });
        } catch (IOReactorException e) {
            System.out.println("Httpclient 初始化异常");
            consumer.accept("");
        }
    }

    /***
     * 查找权限code是否在db的配置中
     * @param parray 需要查询的权限code数组
     *  @param  dparray 数据库配置的权限code数组
     * ***/
    private boolean findPermissionCode(String[] parray, String[] dparray) {
        for (String p : parray) {
            int i = 0;
            for (String p2 : dparray) {
                if (p.equals(p2)) {
                    break;
                }
                i++;
            }
            if (i == dparray.length) {
                return false;
            } else {
                continue;
            }
        }
        return true;
    }

    /**
     * @param dbpermission   当前目录节点设置的用户权限字符串
     * @param permissioncode 需要查询的具备的权限字符串
     ***/
    private String findPermissionCode(String dbpermission, String permissioncode) {
        String[] dbparray = dbpermission.split("_");
        CopyOnWriteArrayList perlist = new CopyOnWriteArrayList(permissioncode.split("_"));
        for (String d : dbparray) {
            for (int i = 0; i < perlist.size(); i++) {
                if (d.equals(perlist.get(i))) {
                    perlist.remove(i);
                }
            }
        }
        String result = "";
        if (perlist.isEmpty()) {
            return "";
        } else {
            for (int i = 0; i < perlist.size(); i++) {
                if (i == 0) {
                    result = perlist.get(i).toString();
                } else if (i == perlist.size() - 1) {
                    result = result + "_" + perlist.get(i).toString();
                }
            }
        }
        return result;
    }

    /**
     * 查找hdfs权限
     *
     * @param dbFolderPermission
     ***/
    private boolean findHdfsPermission(HdfsFolder dbFolderPermission, CopyOnWriteArrayList hdfspath, String permissioncodes) {
        for (int i = 0; i < hdfspath.size(); i++) {
            for (Map.Entry<String, HdfsFolder> e : dbFolderPermission.getChildFolders().entrySet()) {
                if (hdfspath.get(i).toString().equals(e.getKey())) {
                    String pstr = e.getValue().getFolderPermissions();
                    if (pstr != null && !pstr.isEmpty()) {
                        if (pstr.equals("all")) {
                            return true;
                        } else {
                            String findstr = findPermissionCode(pstr, permissioncodes);
                            if (!findstr.isEmpty()) {
                                String tempstr = hdfspath.get(0).toString();
                                hdfspath.remove(0);
                                return findHdfsPermission(dbFolderPermission.getChildFolders().get(tempstr), hdfspath, findstr);
                            } else {
                                return true;
                            }
                        }
                    } else {
                        String tempstr = hdfspath.get(0).toString();
                        hdfspath.remove(0);
                        return findHdfsPermission(dbFolderPermission.getChildFolders().get(tempstr), hdfspath, permissioncodes);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 处理hdfs文件夹
     *
     * @param folderpermissioncodes 目录权限
     * @param folders               当前要处理的目录
     * @param hdfsfolders           历史目录
     * @param parent                父级目录
     **/
    private void findfolder(CopyOnWriteArrayList folders, Map<String, HdfsFolder> hdfsfolders, String folderpermissioncodes, HdfsFolder parent) {
        for (int i = 0; i < folders.size(); i++) {
            if (hdfsfolders != null) {
                if (hdfsfolders.containsKey(folders.get(i))) {
                    if (folders.size() == i + 1) {
                        HdfsFolder hdfsfolder = hdfsfolders.get(folders.get(i));
                        String newpermissioncodes = combinationarray(hdfsfolder.getFolderPermissions(), folderpermissioncodes);
                        hdfsfolder.setFolderPermissions(newpermissioncodes);
                    } else {
                        String tempfoldername = folders.get(i).toString();
                        folders.remove(i);
                        findfolder(folders, hdfsfolders.get(tempfoldername).getChildFolders()
                                , folderpermissioncodes, hdfsfolders.get(tempfoldername));
                    }
                } else {
                    HdfsFolder chdfsfolder = new HdfsFolder();
                    chdfsfolder.setFolderName(folders.get(i).toString());
                    chdfsfolder.setFolderPermissions("");
                    if (parent != null) {
                        if (parent.getChildFolders() == null) {
                            parent.setChildFolders(new LinkedHashMap());
                        }
                        if (folders.size() == i + 1) {
                            chdfsfolder.setFolderPermissions(folderpermissioncodes);
                            if (parent.getChildFolders().containsKey(folders.get(i).toString())) {
                                HdfsFolder temphf = parent.getChildFolders().get(folders.get(i).toString());
                                if (temphf.getFolderPermissions() != null && !temphf.getFolderPermissions().isEmpty()) {
                                    temphf.setFolderPermissions(combinationarray(temphf.getFolderPermissions(), folderpermissioncodes));
                                    parent.getChildFolders().replace(folders.get(i).toString(), chdfsfolder);
                                } else {
                                    parent.getChildFolders().replace(folders.get(i).toString(), chdfsfolder);
                                }

                            } else {
                                if (parent.getChildFolders() == null) {
                                    parent.setChildFolders(new LinkedHashMap());
                                }
                                parent.getChildFolders().putIfAbsent(folders.get(i).toString(), chdfsfolder);
                            }
                        } else {
                            parent.getChildFolders().putIfAbsent(folders.get(i).toString(), chdfsfolder);
                        }

                    } else {
                        if (folders.size() == i + 1) {
                            chdfsfolder.setFolderPermissions(folderpermissioncodes);
                        }
                        hdfsfolders.putIfAbsent(folders.get(i).toString(), chdfsfolder);
                    }
                    folders.remove(i);
                    findfolder(folders, hdfsfolders, folderpermissioncodes, chdfsfolder);
                }
            } else {
                HdfsFolder chdfsfolder = new HdfsFolder();
                chdfsfolder.setChildFolders(new LinkedHashMap());
                chdfsfolder.setFolderName(folders.get(i).toString());
                if (parent.getChildFolders() == null) {
                    parent.setChildFolders(new LinkedHashMap());
                }
                parent.getChildFolders().putIfAbsent(folders.get(i).toString(), chdfsfolder);
                folders.remove(i);
                findfolder(folders, null, folderpermissioncodes, chdfsfolder);

            }
        }
    }

    /***
     * 合并权限字符串
     * **/
    private String combinationarray(String source, String search) {
        String[] searchArray = search.split("_");
        for (String s : searchArray) {
            if (source.contains("all")) {
                //todo
            } else {
                if (s.contains("all")) {
                    source = "all";
                } else {
                    if (!source.contains(s)) {
                        if (!source.isEmpty()) {
                            source = source + "_" + s;
                        } else {
                            source = s;
                        }
                    }
                }

            }

        }
        return source;
    }

    /**
     * hdfs 目录节点权限类
     **/
    public class HdfsFolder {

        private String folderName;
        private String folderPermissions;
        private Map<String, HdfsFolder> childFolders;

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        public String getFolderName() {
            return this.folderName;
        }

        public void setFolderPermissions(String folderPermissions) {
            this.folderPermissions = folderPermissions;
        }

        public String getFolderPermissions() {
            return this.folderPermissions;
        }

        public void setChildFolders(Map childFolders) {
            this.childFolders = childFolders;
        }

        public Map<String, HdfsFolder> getChildFolders() {
            return this.childFolders;
        }


    }

}



