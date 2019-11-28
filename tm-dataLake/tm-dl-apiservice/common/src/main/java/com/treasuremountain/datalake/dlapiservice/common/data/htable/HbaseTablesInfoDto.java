package com.treasuremountain.datalake.dlapiservice.common.data.htable;

import com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex.IndexlogDto;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/6/17.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Data
public class HbaseTablesInfoDto {

    private String hbtableId;
    private String hbtableName;//虚拟表名
    private HashMap<String,TableLogDto> hbtableLogName;//真实表名
    private HashMap<String, IndexlogDto> hbtableIndexName;//真实索引
    private HashMap<String,HBcolumnfamilyConfigDto> HbColumnFamilys;//列族信息
}
