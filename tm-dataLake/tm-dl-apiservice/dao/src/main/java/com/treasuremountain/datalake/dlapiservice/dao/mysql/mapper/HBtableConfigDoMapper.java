package com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper;

import com.treasuremountain.datalake.dlapiservice.common.data.htable.HBtableConfigDto;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.HBtableConfigDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HBtableConfigDoMapper {
    int deleteByPrimaryKey(String hbtableId);

    int insert(HBtableConfigDo record);

    int insertSelective(HBtableConfigDo record);

    HBtableConfigDo selectByPrimaryKey(String hbtableId);

    HBtableConfigDo selectByRealTableName(String realName);

    List<HBtableConfigDo> selectAll();

    int updateByPrimaryKeySelective(HBtableConfigDo record);

    int updateByPrimaryKey(HBtableConfigDo record);

    List<HBtableConfigDo> selectSelective(HBtableConfigDo record);

    List<HBtableConfigDo> selectByPrimaryKeyList(List<String> list);

    int updateByPrimaryKeyDelete(HBtableConfigDo record);

    HBtableConfigDto selectAllInfoByPrimaryKey(String hbtableId);

    /**
     * 更新初始化标识栏位
     **/
    int updateInittag(String hbtableId);


    /**
     * 查看是否存在索引数据
     *
     * @param hbtableId
     * @return
     */
    int selectByPrimaryKeyIsindex(String hbtableId);

    int updateCurrentIndexByHtid(@Param("hbtableId") String hbtableId, @Param("indexname") String indexname);

    int updateCurrentTableNameByHtid(@Param("hbtableId") String hbtableId, @Param("tname") String tname);


}