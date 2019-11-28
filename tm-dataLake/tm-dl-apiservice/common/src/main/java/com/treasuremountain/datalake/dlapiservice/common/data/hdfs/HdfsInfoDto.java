package com.treasuremountain.datalake.dlapiservice.common.data.hdfs;

import lombok.Data;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/6/17.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Data
public class HdfsInfoDto {

    private String blockSize;
    private String length;
    private String modificationTime;//yyyy-MM-dd HH:mm:ss
    private String replication;
    private String type; //FILE:文件  DIRECTORY：目录
    private String pathSuffix;// 文件名或目录名
}
