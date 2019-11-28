package com.treasuremountain.datalake.dlapiservice.service.file;

import com.treasuremountain.datalake.dlapiservice.controller.BaseController;
import com.treasuremountain.tmcommon.thirdpartyservice.hdfs.TMDLHDFSFilePath;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.treasuremountain.datalake.dlapiservice.common.data.hdfs.HdfsInfoDto;
import com.treasuremountain.tmcommon.thirdpartyservice.hdfs.TMDLHDFSOperator;
import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    @Autowired
    private BaseController baseController;

    public List<TMDLHDFSFilePath> baseFileList(String filepath) throws Exception {
        return TMDLHDFSOperator.baseFileList(filepath, baseController.getCurrentUser());
    }

    public void baseFileUpload(String filepath, String action, InputStream inputStream) throws Exception {
        TMDLHDFSOperator.baseFileUpload(filepath, action, inputStream, baseController.getCurrentUser());
    }

    public void baseFileDownload(String filepath, OutputStream out) throws Exception {
        TMDLHDFSOperator.baseFileDownLoad(filepath, out, baseController.getCurrentUser());
    }

    public int getFileLength(String filepath) throws Exception {
        return TMDLHDFSOperator.getFileLength(filepath, baseController.getCurrentUser());
    }

    public String getFileName(String filepath) {
        return TMDLHDFSOperator.getFileName(filepath);
    }

    public void baseDeleteFile(String filePath) throws Exception {
        TMDLHDFSOperator.baseDeleteFile(filePath, baseController.getCurrentUser());
    }

    public Boolean baseMkDir(String filePath) throws Exception {
        return TMDLHDFSOperator.mkDir(filePath, baseController.getCurrentUser());
    }

    public Boolean baseRmDir(String filePath) throws Exception {
        return TMDLHDFSOperator.rmDir(filePath, baseController.getCurrentUser());
    }

    public Boolean baseRename(String srcPath, String dstPath) throws Exception {
        return TMDLHDFSOperator.rename(srcPath, dstPath, baseController.getCurrentUser());
    }

    public List<HdfsInfoDto> listFileStatus(String path, Integer size, Integer index) throws Exception {
        FileStatus[] fileStatuses = TMDLHDFSOperator.listFileStatus(path, baseController.getCurrentUser());
        if (fileStatuses.length > 0) {
            List<HdfsInfoDto> hdfsInfoDtoList = new ArrayList<>();
            for (FileStatus status : fileStatuses) {
                HdfsInfoDto hdfsInfoDto = new HdfsInfoDto();
                hdfsInfoDto.setBlockSize(String.valueOf(status.getBlockSize()));
                hdfsInfoDto.setLength(String.valueOf(status.getLen()));
                hdfsInfoDto.setModificationTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(status.getModificationTime()));
                hdfsInfoDto.setReplication(String.valueOf(status.getReplication()));
                String[] paths = status.getPath().toString().split("/");
                hdfsInfoDto.setPathSuffix(paths[paths.length - 1]);
                String type = status.isDirectory() ? "DIRECTORY" : "FILE";
                hdfsInfoDto.setType(type);
                hdfsInfoDtoList.add(hdfsInfoDto);
            }
            Integer fromIndex = (index - 1) * size;
            Integer toIndex = fromIndex + size;
            if (toIndex >= (hdfsInfoDtoList.size() - 1)) toIndex = hdfsInfoDtoList.size();

            List<HdfsInfoDto> result = new ArrayList<>();
            if (fromIndex == toIndex) {
                result.add(hdfsInfoDtoList.get(fromIndex));
            } else {
                result = hdfsInfoDtoList.subList(fromIndex, toIndex);
            }
            return result;
        }
        return null;
    }
}
