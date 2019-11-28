package com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex;

import lombok.Data;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/6/17.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Data
public class IndexDetailDto {


    private String health;
    private String status;
    private String primary;
    private String primary_size;
    private String documents_deleted;
    private String documents;
    private String name;
    private String replica;
    private String size;
    private String uuid;
    private String settings;
    private String mapping;
}
