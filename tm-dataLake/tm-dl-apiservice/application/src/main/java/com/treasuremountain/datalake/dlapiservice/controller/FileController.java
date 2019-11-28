package com.treasuremountain.datalake.dlapiservice.controller;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.common.data.hdfs.HdfsInfoDto;
import com.treasuremountain.datalake.dlapiservice.common.message.HttpResponseMsg;
import com.treasuremountain.datalake.dlapiservice.config.webconfig.ApiVersion;
import com.treasuremountain.datalake.dlapiservice.service.file.FileService;
import com.treasuremountain.tmcommon.thirdpartyservice.hdfs.TMDLHDFSFilePath;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMDataFlowStep;
import com.treasuremountain.tmcommon.thirdpartyservice.log.TMlogMaker;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


@RestController
@RequestMapping("/api/dlapiservice/{version}/file")
public class FileController extends BaseController {

    private final static Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private Gson gson;

    @Autowired
    private FileService fileService;


    @RequestMapping(value = "/base", method = RequestMethod.POST)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg fileUpload(@RequestParam("file") MultipartFile file,
                                      @RequestParam("filepath") String filepath,
                                      @RequestParam("action") String action,
                                      @RequestHeader("User-Agent") String userAgent) throws IOException {

//        String fileInfo = String.format("filepath:%s action:%s", filepath, action);

        try {
//            String logStr1 = TMlogMaker.dataFlow("", getRsquestInfoStr() + userAgent,
//                    fileInfo, TMDataFlowStep.DLFileupload, "is received", 1);
//            log.info(logStr1);

            // filepath = "/dptemp/" + filepath; //delete by ref.tian

            fileService.baseFileUpload(filepath, action, file.getInputStream());

//            String logStr2 = TMlogMaker.dataFlow("", getRsquestInfoStr() + userAgent,
//                    fileInfo, TMDataFlowStep.DLFileupload, "successed", 1);
//            log.info(logStr2);

            return ResponseUtil.created(super.response, "created", null);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/userdata", method = RequestMethod.POST)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg fileUserUpload(@RequestParam("file") MultipartFile file,
                                          @RequestParam("userId") String userId,
                                          @RequestParam("filepath") String filepath,
                                          @RequestParam("action") String action,
                                          @RequestHeader("User-Agent") String userAgent) throws IOException {

        try {
            filepath = String.format("/dpuserdata/%s/%s", userId, filepath);

            fileService.baseFileUpload(filepath, action, file.getInputStream());

            return ResponseUtil.created(super.response, "created", null);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    @RequestMapping(value = "/userdata/{userId}/list", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg fileUserList(@PathVariable("userId") String userId,
                                        @RequestParam("filepath") String filepath,
                                        @RequestHeader("User-Agent") String userAgent) {

        try {
            filepath = String.format("/dpuserdata/%s/%s", userId, filepath);

            List<TMDLHDFSFilePath> rlist = fileService.baseFileList(filepath);

            return ResponseUtil.ok(super.response, "", rlist);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    @RequestMapping(value = "/userdata/{userId}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public String fileUserDownLoad(@PathVariable("userId") String userId,
                                   @RequestParam("filepath") String filepath,
                                   HttpServletResponse response) {
        try {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileService.getFileName(filepath));

            String realPath = String.format("/dpuserdata/%s/%s", userId, filepath);

            OutputStream os = response.getOutputStream();
            fileService.baseFileDownload(realPath, os);

            return "download success";
        } catch (Exception ex) {
            return ex.toString();
        }
    }


    @RequestMapping(value = "/userdata/{userId}", method = RequestMethod.DELETE)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg fileUserDelete(@PathVariable("userId") String userId,
                                          @RequestParam("filepath") String filepath) {
        try {
            String realPath = String.format("/dpuserdata/%s/%s", userId, filepath);

            fileService.baseDeleteFile(realPath);

            return ResponseUtil.deleted(super.response, "deleted", null);
        } catch (Exception ex) {
            String error = ex.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    // hdfs begin

    @RequestMapping(value = "/hdfs/{userid}", method = RequestMethod.GET)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg<List<HdfsInfoDto>> hdfsDirInfos(@PathVariable(value = "userid",required = true)String userId,
                                                           @RequestParam(value = "dirpath",required = false)String dirPath,
                                                           @RequestParam(value = "pagesize",required = true)Integer pageSize,
                                                           @RequestParam(value = "pageindex",required = true)Integer pageindex){
        HttpResponseMsg httpResponseMsg = new HttpResponseMsg();
        try {
            if (StringUtils.isEmpty(dirPath)){
                dirPath = String.format("/dpuserdata/%s", userId);
            }else {
                dirPath = String.format("/dpuserdata/%s/%s", userId, dirPath);
            }
            List<HdfsInfoDto> hdfsInfoDtoList = fileService.listFileStatus(dirPath,pageSize,pageindex);
            httpResponseMsg.setData(hdfsInfoDtoList);
            return httpResponseMsg;
        }catch (Exception e){
            String error = e.toString();
            log.error(error);
            httpResponseMsg.setMsg(e.getMessage());
            return httpResponseMsg;
        }
    }



    @RequestMapping(value = "/hdfs/directory/{userid}", method = RequestMethod.POST)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg<Boolean> mkdir(@PathVariable(value = "userid",required = true)String userid,
                                          @RequestParam(value = "parentdir",required = true)String parentdir,
                                          @RequestParam(value = "dirname",required = true)String dirname){
        try {
            String dirPath = String.format("/dpuserdata/%s/%s/%s", userid, parentdir,dirname);
            fileService.baseMkDir(dirPath);
            return ResponseUtil.created(super.response, "created", null);
        }catch (Exception e){
            String error = e.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/hdfs/directory/{userid}", method = RequestMethod.PUT)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg<Boolean> editDir(@PathVariable(value = "userid",required = true)String userid,
                                            @RequestParam(value = "olddir",required = true)String olddir,
                                            @RequestParam(value = "newdirname",required = true)String newdirname){
        try {
            String oldDirPath = String.format("/dpuserdata/%s/%s", userid, olddir);
            File file = new File(oldDirPath);
            String parentPath = file.getParent();
            String newDirPath = parentPath+"/"+ newdirname;
//            String newDirPath = String.format("/dpuserdata/%s/%s", userid, newdirname);
            fileService.baseRename(oldDirPath,newDirPath);
            return ResponseUtil.created(super.response, "renamed", null);
        }catch (Exception e){
            String error = e.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/hdfs/directory/{userid}", method = RequestMethod.DELETE)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg<Boolean> rmDir(@PathVariable(value = "userid",required = true)String userid,
                                          @RequestParam(value = "dir",required = true)String dir){
        try {
            String dirPath = String.format("/dpuserdata/%s/%s", userid, dir);
            fileService.baseRmDir(dirPath);
            return ResponseUtil.deleted(super.response, "deleted", null);
        }catch (Exception e){
            String error = e.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }


    @RequestMapping(value = "/userdata/{userid}", method = RequestMethod.PUT)
    @ApiVersion(1)
    @CrossOrigin
    public HttpResponseMsg<Boolean> editFileName(@PathVariable(value = "userid",required = true)String userid,
                                                 @RequestParam(value = "oldfilename",required = true)String oldfilename,
                                                 @RequestParam(value = "destinationfilename",required = true)String destinationfilename){

        try {
            String oldDirPath = String.format("/dpuserdata/%s/%s", userid, oldfilename);
            String newDirPath = String.format("/dpuserdata/%s/%s", userid, destinationfilename);
            fileService.baseRename(oldDirPath,newDirPath);
            return ResponseUtil.created(super.response, "renamed", null);
        }catch (Exception e){
            String error = e.toString();
            log.error(error);
            return ResponseUtil.internalServerError(super.response, error, null);
        }
    }

    // hdfs end
}
