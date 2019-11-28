package com.treasuremountain.datalake.dlapiservice.dao.mysql.htable;

import com.google.gson.Gson;
import com.treasuremountain.datalake.dlapiservice.common.data.htable.*;
import com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex.IndexlogDto;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.Dao;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.DaoUtil;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.mapper.*;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gerryzhao on 10/20/2018.
 */
@Component
@Slf4j
public class HBtableImpl {
    private Gson gson=new Gson();

    public HBtableConfigDto selectByPrimaryKey(String hbtableId) {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        HBtableConfigDoMapper mapper = session.getMapper(HBtableConfigDoMapper.class);
        HBtableConfigDo configDo = mapper.selectByPrimaryKey(hbtableId);

        session.close();

        return DaoUtil.getData(configDo);
    }

    /**
     * 根据真实表名查询
     *
     * @param tableName
     * @return
     */
    public HBtableConfigDto selectByRealTableName(String tableName) {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        HBtableConfigDoMapper mapper = session.getMapper(HBtableConfigDoMapper.class);
        HBtableConfigDo configDo = mapper.selectByRealTableName(tableName);

        session.close();

        return DaoUtil.getData(configDo);
    }

    /**
     * 查询table下全部的配置数据
     **/
    public HBtableConfigDto selectAllInfoByPrimaryKey(String hbtableId) {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {

            HBtableConfigDoMapper mapper = session.getMapper(HBtableConfigDoMapper.class);
            HBtableConfigDto configDo = mapper.selectAllInfoByPrimaryKey(hbtableId);
            session.close();
            return configDo;
        }
    }

    public List<HBtableConfigDto> selectAll() {
        SqlSession session = Dao.sqlSessionFactory.openSession();

        HBtableConfigDoMapper mapper = session.getMapper(HBtableConfigDoMapper.class);
        List<HBtableConfigDo> doList = mapper.selectAll();

        session.close();

        return DaoUtil.convertDataList(doList);
    }

    public void updateCurrentIndex(String htid, String indexName) {

        SqlSession session = Dao.sqlSessionFactory.openSession();

        HBtableConfigDoMapper mapper = session.getMapper(HBtableConfigDoMapper.class);

        mapper.updateCurrentIndexByHtid(htid, indexName);

        session.commit();
        session.close();
    }

    public void updateCurrentTableName(String htid, String tableName) {

        SqlSession session = Dao.sqlSessionFactory.openSession();

        HBtableConfigDoMapper mapper = session.getMapper(HBtableConfigDoMapper.class);
        mapper.updateCurrentTableNameByHtid(htid, tableName);

        session.commit();
        session.close();
    }

    public int insert(HBtableConfigDto record) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                HBtableConfigDoMapper tableMapper = session.getMapper(HBtableConfigDoMapper.class);
                //region 判断是否有相同的表名
                tableNameCheck(tableMapper, record.getHbtableName());
                //region SetModel
                HBtableConfigDo hBtableConfigDo = new HBtableConfigDo();
                hBtableConfigDo.setHbtableId(record.getHbtableId());
                hBtableConfigDo.setHbtableName(record.getHbtableName());
                hBtableConfigDo.setHbtableIscompression(record.getHbtableIscompression());
                hBtableConfigDo.setHbtableCompressionname(record.getHbtableCompressionname());
                hBtableConfigDo.setHbtableIssplit(record.getHbtableIssplit());
                hBtableConfigDo.setHbtableSplitinfo(record.getHbtableSplitinfo());
                hBtableConfigDo.setHbtableDesc(record.getHbtableDesc());
                hBtableConfigDo.setHbtableIstablesegment(record.getHbtableIstablesegment());
                hBtableConfigDo.setHbtablesegmenttime(record.getHbtablesegmenttime());
                hBtableConfigDo.setHbtableretentiontime(record.getHbtableretentiontime());
                hBtableConfigDo.setHbcurrenttablename(record.getHbcurrenttablename());
                hBtableConfigDo.setHbtableIstwoLevelIndex(false);
                hBtableConfigDo.setHbindexsegmenttime(record.getHbindexsegmenttime());
                hBtableConfigDo.setHbindexretentiontime(record.getHbindexretentiontime());
                hBtableConfigDo.setHbcurrentindexname(record.getHbcurrentindexname());

                hBtableConfigDo.setHbtableIsenable(true);
                //新增默认为false
                hBtableConfigDo.setInitResult(false);
                hBtableConfigDo.setHbtableModifiedby(record.getHbtableCreateby());
                hBtableConfigDo.setHbtableModifieddt(new Date());
                hBtableConfigDo.setHbtableCreateby(record.getHbtableCreateby());
                hBtableConfigDo.setHbtableCreatedt(new Date());
                //endregion
                Integer count = tableMapper.insert(hBtableConfigDo);
                if (count <= 0) {
                    throw new Exception("新增不成功");
                } else {
                    //新增其他表
                    HBcolumnfamilyConfigDoMapper familyMapper = session.getMapper(HBcolumnfamilyConfigDoMapper.class);
                    List<HBcolumnfamilyConfigDto> hBcolumnfamilyConfigDtos = record.getColumnfamilyConfigDtos();
                    HBcolumnConfigDoMapper columnMapper = session.getMapper(HBcolumnConfigDoMapper.class);
                    for (HBcolumnfamilyConfigDto familyFor :
                            hBcolumnfamilyConfigDtos) {
                        //region SetModel
                        HBcolumnfamilyConfigDo familyModel = new HBcolumnfamilyConfigDo();
                        familyModel.setHbtableId(record.getHbtableId());
                        familyModel.setHbcolumnfamilyId(UUID.randomUUID().toString());
                        familyModel.setHbcolumnfamilyName(familyFor.getHbcolumnfamilyName());
                        familyModel.setHbcolumnfamilyIsenable(true);
                        familyModel.setHbcolumnfamilyModifiedby(record.getHbtableCreateby());
                        familyModel.setHbcolumnfamilyModifieddt(new Date());
                        familyModel.setHbcolumnfamilyCreateby(record.getHbtableCreateby());
                        familyModel.setHbcolumnfamilyCreatedt(new Date());
                        //endregion
                        //region 判断名称不存在
                        familyNameCheck(familyMapper, record.getHbtableId(), familyModel.getHbcolumnfamilyName());
                        //endregion
                        count = familyMapper.insert(familyModel);
                        if (count <= 0) {
                            throw new Exception("新增列族【" + familyModel.getHbcolumnfamilyName() + "】不成功");
                        }
                        count = autoColumn(false, columnMapper, record.getHbtableCreateby(), familyModel.getHbcolumnfamilyId(), familyFor.getColumnConfigDtos());
                        if (count <= 0) {
                            throw new Exception("新增列【" + familyModel.getHbcolumnfamilyName() + "】不成功");
                        }
                    }
                }
                count = autoIstwoLevelIndex(tableMapper, record.getHbtableId());
                session.commit();
                return count;

            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (
                Exception ex) {
            throw ex;
        }

    }

    //region 重用代码封装

    //自动处理列
    public int autoColumn(Boolean initResult, HBcolumnConfigDoMapper columnMapper, String hbtableModifiedby, String hbcolumnfamilyId, List<HBcolumnConfigDto> hBcolumnConfigDtos) throws Exception {
        Integer count = 1; //默认成功
        if (hBcolumnConfigDtos != null && hBcolumnConfigDtos.size() > 0) {
            HBcolumnConfigDo columnModel = null;
            HBcolumnConfigDo columnSelectModel = null;
            for (HBcolumnConfigDto columnFor :
                    hBcolumnConfigDtos) {
                columnModel = new HBcolumnConfigDo();
                if (hbcolumnfamilyId == null || hbcolumnfamilyId.isEmpty()) {
                    throw new Exception("列族Id【" + columnFor.getHbcolumnId() + "】不能为空，列名称【" + columnModel.getHbcolumnName() + "】");
                }
                columnModel.setHbcolumnfamilyId(columnFor.getHbcolumnfamilyId());
                columnModel.setHbcolumnId(columnFor.getHbcolumnId());
                columnModel.setHbcolumnIsindex(columnFor.getHbcolumnIsindex());
                columnModel.setHbcolumnName(columnFor.getHbcolumnName());
                columnModel.sethbcolumnDesc(columnFor.getHbcolumnDesc());
                columnModel.setHbcolumnIsenable(true);
                columnModel.setHbcolumnModifiedby(hbtableModifiedby);
                columnModel.setHbcolumnModifieddt(new Date());
                columnModel.setHbcolumnType(columnFor.getHbcolumnType());
                if (columnFor.getStatus() == null) {
                    columnFor.setStatus("");
                }
                log.debug("当前修改信息为："+gson.toJson(columnModel));
                switch (columnFor.getStatus()) {
                    case "add":
                        //region 判断名称不存在
                        columnNameCheck(columnMapper, hbcolumnfamilyId, columnFor.getHbcolumnName());
                        //endregion
                        columnModel.setHbcolumnId(UUID.randomUUID().toString());
                        columnModel.setHbcolumnfamilyId(hbcolumnfamilyId);
                        columnModel.setHbcolumnCreateby(hbtableModifiedby);
                        columnModel.setHbcolumnCreatedt(new Date());
                        count = columnMapper.insert(columnModel);
                        if (count <= 0) {
                            throw new Exception("新增列【" + columnModel.getHbcolumnName() + "】不成功");
                        }
                        break;
                    case "edit":
                        if (initResult) {
                            throw new Exception("初始化状态为true不能修改列族");
                        }
                        columnSelectModel = new HBcolumnConfigDo();
                        columnSelectModel.setHbcolumnId(columnModel.getHbcolumnId());
                        List<HBcolumnConfigDo> hBcolumnConfigDos = columnMapper.selectSelective(columnSelectModel);
                        if (hBcolumnConfigDos == null || hBcolumnConfigDos.size() <= 0) {
                            throw new Exception("修改列不存在");
                        } else {
                            //判断isResult是否为ture
                            columnSelectModel = hBcolumnConfigDos.get(0);
                            //判断是否有重复表名
                            //判断之前的表名跟现在的表名是否相同，如果不同就进行查询新表明是否存在，如果相同则不需要查询
                            if (!(columnSelectModel.getHbcolumnName().toString().equals(columnModel.getHbcolumnName().toString()))) {
                                columnNameCheck(columnMapper, columnModel.getHbcolumnfamilyId(), columnModel.getHbcolumnName());
                            }
                            count = columnMapper.updateByPrimaryKeySelective(columnModel);
                            if (count < 0) {
                                throw new Exception("修改列【" + columnModel.getHbcolumnId() + "】不成功");
                            }
                        }
                        break;
                    case "delete":
                        if (initResult) {
                            throw new Exception("初始化状态为true不能删除列族");
                        }
                        columnModel = new HBcolumnConfigDo();
                        columnModel.setHbcolumnId(columnModel.getHbcolumnId());
                        columnModel.setHbcolumnIsenable(false);
                        count = columnMapper.updateSelective(columnModel);
                        if (count < 0) {
                            throw new Exception("删除列【" + columnModel.getHbcolumnId() + "】不成功");
                        }
                        break;
                    case "":
                        break;
                    default:
                        throw new Exception("Status异常(add/edit/delete/)，不修改请置空" + columnFor.getStatus());
                }
            }
        }
        //endregion
        return count;
    }

    //table检查
    public void tableNameCheck(HBtableConfigDoMapper tableMapper, String hbtableName) throws Exception {
        HBtableConfigDo selectModel = new HBtableConfigDo();
        if (hbtableName == null || hbtableName.isEmpty()) {
            throw new Exception("表名不能为空");
        }
        selectModel.setHbtableName(hbtableName);
        selectModel.setHbtableIsenable(true);
        List<HBtableConfigDo> hBtableConfigDos = tableMapper.selectSelective(selectModel);
        if (hBtableConfigDos != null && hBtableConfigDos.size() > 0) {
            throw new Exception("表名【" + hbtableName + "】已经存在");
        }
    }

    //列族检查
    public void familyNameCheck(HBcolumnfamilyConfigDoMapper familyMapper, String hbtableId, String hbcolumnfamilyName) throws Exception {
        //region   新增列族和列
        //判断名称不存在
        HBcolumnfamilyConfigDo familySelectModel = new HBcolumnfamilyConfigDo();
        familySelectModel.setHbtableId(hbtableId);
        if (hbcolumnfamilyName == null || hbcolumnfamilyName.isEmpty()) {
            throw new Exception("列族名称不能为空");
        }
        familySelectModel.setHbcolumnfamilyName(hbcolumnfamilyName);
        familySelectModel.setHbcolumnfamilyIsenable(true);
        List<HBcolumnfamilyConfigDo> familySelectDos = familyMapper.selectSelective(familySelectModel);
        if (familySelectDos != null && familySelectDos.size() > 0) {
            throw new Exception("操作列族【" + hbcolumnfamilyName + "】已经存在");
        }
        //endregion
    }

    //列检查
    public void columnNameCheck(HBcolumnConfigDoMapper columnMapper, String hbcolumnfamilyId, String hbcolumnName) throws Exception {
        HBcolumnConfigDo columnSelectModel = new HBcolumnConfigDo();
        columnSelectModel.setHbcolumnfamilyId(hbcolumnfamilyId);
        if (hbcolumnName == null || hbcolumnName.isEmpty()) {
            throw new Exception("列名不能为空");
        }
        columnSelectModel.setHbcolumnName(hbcolumnName);
        columnSelectModel.setHbcolumnIsenable(true);
        List<HBcolumnConfigDo> columnSelectDos = columnMapper.selectSelective(columnSelectModel);
        if (columnSelectDos != null && columnSelectDos.size() > 0) {
            throw new Exception("操作列不成功:列名称【" + columnSelectModel.getHbcolumnName() + "】已经存在");
        }
    }

    //自动分析或修改HbtableIstwoLevelIndex
    public int autoIstwoLevelIndex(HBtableConfigDoMapper tableMapper, String hbtableId) throws Exception {
        int count = 1;
        //查询是否有columnindex是否未索引的
        if (tableMapper.selectByPrimaryKeyIsindex(hbtableId) > 0) {

            //修改索引栏位为ture
            HBtableConfigDo hBtableConfigUpdateModel = new HBtableConfigDo();
            hBtableConfigUpdateModel.setHbtableId(hbtableId);
            hBtableConfigUpdateModel.setHbtableIstwoLevelIndex(true);
            count = tableMapper.updateByPrimaryKeySelective(hBtableConfigUpdateModel);
            if (count <= 0) {
                throw new Exception("修改【HbtableIstwoLevelIndex】不成功");
            }
        }
        return count;
    }

    //endregion

    public Integer updateByPrimaryKey(HBtableConfigDto record) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {

                Integer count = 0;
                HBtableConfigDoMapper tableMapper = session.getMapper(HBtableConfigDoMapper.class);
                HBcolumnfamilyConfigDoMapper familyMapper = session.getMapper(HBcolumnfamilyConfigDoMapper.class);
                HBcolumnConfigDoMapper columnMapper = session.getMapper(HBcolumnConfigDoMapper.class);
                //查询数据
                HBtableConfigDo selectModel = new HBtableConfigDo();
                selectModel.setHbtableId(record.getHbtableId());
                selectModel.setHbtableIsenable(true);
                List<HBtableConfigDo> hBtableConfigDos = tableMapper.selectSelective(selectModel);
                if (hBtableConfigDos == null || hBtableConfigDos.size() <= 0) {
                    throw new Exception("表Id【" + record.getHbtableId() + "】不存在");
                } else {
                    //判断isResult是否为ture
                    selectModel = hBtableConfigDos.get(0);
                    Boolean initResult = selectModel.getInitResult();
                    if (initResult == null) {
                        initResult = false;
                    }
                    //判断是否有重复表名
                    //判断之前的表名跟现在的表名是否相同，如果不同就进行查询新表明是否存在，如果相同则不需要查询
                    if (!(record.getHbtableName().toString().equals(selectModel.getHbtableName().toString()))) {
                        tableNameCheck(tableMapper, record.getHbtableName());
                    }
                    //判断是否需要新增列族
                    List<HBcolumnfamilyConfigDto> hBcolumnfamilyConfigDtos = record.getColumnfamilyConfigDtos();
                    if (hBcolumnfamilyConfigDtos != null && hBcolumnfamilyConfigDtos.size() > 0) {

                        //需要把删除的数据先拉出来，后面优化
                        for (HBcolumnfamilyConfigDto familyFor :
                                hBcolumnfamilyConfigDtos) {
                            HBcolumnfamilyConfigDo familyModel = new HBcolumnfamilyConfigDo();
                            familyModel.setHbtableId(familyFor.getHbtableId());
                            if (!(record.getHbtableId().equals(familyFor.getHbtableId()))) {
                                throw new Exception("表Id【" + record.getHbtableId() + "】与列族Id【" + familyFor.getHbtableId() + "】不相同");
                            }
                            if (familyFor.getHbcolumnfamilyName() == null || familyFor.getHbcolumnfamilyName().isEmpty()) {
                                throw new Exception("列族名不能为空");
                            }
                            familyModel.setHbcolumnfamilyName(familyFor.getHbcolumnfamilyName());
                            familyModel.setHbcolumnfamilyId(familyFor.getHbcolumnfamilyId());
                            familyModel.setHbcolumnfamilyIsenable(true);
                            familyModel.setHbcolumnfamilyModifiedby(familyFor.getHbcolumnfamilyModifiedby());
                            familyModel.setHbcolumnfamilyModifieddt(new Date());
                            List<HBcolumnConfigDto> hBcolumnConfigDtos = familyFor.getColumnConfigDtos();
                            if (familyFor.getStatus() == null || familyFor.getStatus().isEmpty()) {
                                familyFor.setStatus("");
                            }
                            switch (familyFor.getStatus()) {
                                case "add": //如果是新增，那么列族下的列肯定都是新增
                                    if (initResult) {
                                        throw new Exception("初始化状态为true不能新增列族");
                                    }
                                    //新增前判断是否有同名的
                                    familyNameCheck(familyMapper, record.getHbtableId(), familyModel.getHbcolumnfamilyName());
                                    //region   新增列族和列
                                    familyModel.setHbcolumnfamilyId(UUID.randomUUID().toString());
                                    familyModel.setHbcolumnfamilyCreateby(record.getHbtableModifiedby());
                                    familyModel.setHbcolumnfamilyCreatedt(new Date());
                                    count = familyMapper.insert(familyModel);
                                    if (count <= 0) {
                                        throw new Exception("新增列族" + familyModel.getHbcolumnfamilyName() + "不成功");
                                    }
                                    count = autoColumn(initResult, columnMapper, record.getHbtableModifiedby(), familyModel.getHbcolumnfamilyId(), hBcolumnConfigDtos);
                                    break;
                                case "delete":  //下面所有的数据都需要进行delete
                                    if (initResult) {
                                        throw new Exception("初始化状态为true不能删除列族");
                                    }
                                    if (!(record.getHbtableId().equals(familyFor.getHbtableId()))) {
                                        throw new Exception("删除的列族表Id【" + familyFor.getHbtableId() + "】与表Id【" + record.getHbtableId() + "】不同");
                                    }
                                    familyModel = new HBcolumnfamilyConfigDo();
                                    familyModel.setHbcolumnfamilyId(familyFor.getHbcolumnfamilyId());
                                    familyModel.setHbtableId(record.getHbtableId());
                                    familyModel.setHbcolumnfamilyIsenable(false);
                                    count = familyMapper.updateSelective(familyModel);
                                    if (count <= 0) {
                                        throw new Exception("删除列族【" + familyModel.getHbcolumnfamilyId() + "】不成功");
                                    }
                                    //删除下面所有列
                                    HBcolumnConfigDo columnModel = new HBcolumnConfigDo();
                                    columnModel.setHbcolumnfamilyId(familyModel.getHbcolumnfamilyId());
                                    columnModel.setHbcolumnIsenable(false);
                                    columnMapper.updateSelective(columnModel);
                                    //不需要返回值，有数据肯定就删除成功，没有删除成功数据库回报错
                                    break;
                                case "edit":
                                    if (initResult) {
                                        throw new Exception("初始化状态为true不能修改列族");
                                    }
                                    //判断列族名称是否重复
                                    if (!(familyModel.getHbcolumnfamilyName().toString().equals(familyModel.getHbcolumnfamilyName().toString()))) {
                                        familyNameCheck(familyMapper, record.getHbtableId(), familyModel.getHbcolumnfamilyName());
                                    }
                                    //region   新增列族和列
                                    count = familyMapper.updateByPrimaryKeySelective(familyModel);
                                    if (count <= 0) {
                                        throw new Exception("修改列族【" + familyModel.getHbcolumnfamilyId() + "】不成功");
                                    }
                                    //操作列
                                    count = autoColumn(initResult, columnMapper, record.getHbtableModifiedby(), familyModel.getHbcolumnfamilyId(), hBcolumnConfigDtos);
                                    break;
                                case "":
                                    count = autoColumn(initResult, columnMapper, record.getHbtableModifiedby(), familyModel.getHbcolumnfamilyId(), hBcolumnConfigDtos);
                                    break;
                                default:
                                    throw new Exception("Status异常(add/edit/delete/)，不修改请置空" + familyFor.getStatus());
                            }
                        }
                    }
                    HBtableConfigDo hBtableConfigDo = new HBtableConfigDo();
                    //region 主表HBtableConfig修改
                    if (initResult) {   //已初始化过，只能新增列，不能新增列族
                        record.setHbtableName(null);
                        record.setHbtableIscompression(null);
                        record.setHbtableCompressionname(null);
                        record.setHbtableIssplit(null);
                        record.setHbtableSplitinfo(null);
                        //创建信息
                        record.setHbtableCreateby(null);
                        record.setHbtableCreatedt(null);
                        hBtableConfigDo.setHbtableIstwoLevelIndex(record.getHbtableIstwoLevelIndex());
                    } else {
                        //edit by ref.tian
                        if (tableMapper.selectByPrimaryKeyIsindex(record.getHbtableId()) > 0) {
                            hBtableConfigDo.setHbtableIstwoLevelIndex(true);
                        } else {
                            hBtableConfigDo.setHbtableIstwoLevelIndex(false);
                        }
                    }

                    hBtableConfigDo.setHbtableId(record.getHbtableId());
                    hBtableConfigDo.setHbtableName(record.getHbtableName());
                    hBtableConfigDo.setHbtableIscompression(record.getHbtableIscompression());
                    hBtableConfigDo.setHbtableCompressionname(record.getHbtableCompressionname());
                    hBtableConfigDo.setHbtableIssplit(record.getHbtableIssplit());
                    hBtableConfigDo.setHbtableSplitinfo(record.getHbtableSplitinfo());
                    hBtableConfigDo.setHbtableDesc(record.getHbtableDesc());
                    hBtableConfigDo.setHbtableIstablesegment(record.getHbtableIstablesegment());
                    hBtableConfigDo.setHbtablesegmenttime(record.getHbtablesegmenttime());
                    hBtableConfigDo.setHbtableretentiontime(record.getHbtableretentiontime());
                    hBtableConfigDo.setHbcurrenttablename(record.getHbcurrenttablename());

                    hBtableConfigDo.setHbindexsegmenttime(record.getHbindexsegmenttime());
                    hBtableConfigDo.setHbindexretentiontime(record.getHbindexretentiontime());
                    hBtableConfigDo.setHbcurrentindexname(record.getHbcurrentindexname());
                    hBtableConfigDo.setHbtableIsenable(record.getHbtableIsenable());
                    hBtableConfigDo.setInitResult(null);//不用更新这个栏位
                    hBtableConfigDo.setHbtableModifiedby(record.getHbtableModifiedby());
                    hBtableConfigDo.setHbtableModifieddt(new Date());
                    count = tableMapper.updateByPrimaryKeySelective(hBtableConfigDo);
                    if (count <= 0) {
                        throw new Exception("修改不成功");
                    }
                    //endregion
                    session.commit();
                    return count;
                }
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    //软删除
    public Integer deleteByPrimaryKey(String id) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                Integer count = 0;
                HBtableConfigDoMapper mapper = session.getMapper(HBtableConfigDoMapper.class);
                HBcolumnfamilyConfigDoMapper familyMapper = session.getMapper(HBcolumnfamilyConfigDoMapper.class);
                HBcolumnConfigDoMapper columnMapper = session.getMapper(HBcolumnConfigDoMapper.class);

                HBtableConfigDo hBtableModel = new HBtableConfigDo();
                hBtableModel.setHbtableId(id);
                hBtableModel.setHbtableIsenable(false);
                count = mapper.updateByPrimaryKeyDelete(hBtableModel);
                if (count < 1) {
                    count++;
                    //throw new Exception("删除表【" + id + "】不存在或不成功");
                }

                //获取列族
                HBcolumnfamilyConfigDo familySelect = new HBcolumnfamilyConfigDo();
                familySelect.setHbtableId(id);
                familySelect.setHbcolumnfamilyIsenable(true);
                List<HBcolumnfamilyConfigDo> hBcolumnfamilyConfigDtos = familyMapper.selectSelective(familySelect);
                if (hBcolumnfamilyConfigDtos != null || hBcolumnfamilyConfigDtos.size() > 0) {
                    //删除列
                    for (HBcolumnfamilyConfigDo familyFor :
                            hBcolumnfamilyConfigDtos) {
                        HBcolumnConfigDo columnModel = new HBcolumnConfigDo();
                        columnModel.setHbcolumnfamilyId(familyFor.getHbcolumnfamilyId());
                        columnModel.setHbcolumnIsenable(false);
                        columnMapper.updateSelective(columnModel);
                    }
                    //删除列族
                    HBcolumnfamilyConfigDo familyModel = new HBcolumnfamilyConfigDo();
                    familyModel.setHbtableId(id);
                    familyModel.setHbcolumnfamilyIsenable(false);
                    familyMapper.updateSelective(familyModel);
                }
                session.commit();
                return count;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<HBtableConfigDto> selectByPrimaryKeyList(List<String> list) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {

                HBtableConfigDoMapper hbtableMapper = session.getMapper(HBtableConfigDoMapper.class);
                List<HBtableConfigDo> businessConfigDos = hbtableMapper.selectByPrimaryKeyList(list);
                HBcolumnfamilyConfigDoMapper familyMapper = session.getMapper(HBcolumnfamilyConfigDoMapper.class);
                HBcolumnConfigDoMapper columnMapper = session.getMapper(HBcolumnConfigDoMapper.class);
                if (businessConfigDos != null && businessConfigDos.size() > 0) {
                    List<HBtableConfigDto> hBtableConfigDtos = new ArrayList();
                    for (HBtableConfigDo hBtableConfigDo :
                            businessConfigDos) {
                        HBtableConfigDto hBtableConfigDto = hBtableConfigDo.toData();
                        HBcolumnfamilyConfigDo familySelectModel = new HBcolumnfamilyConfigDo();
                        familySelectModel.setHbtableId(hBtableConfigDo.getHbtableId());
                        familySelectModel.setHbcolumnfamilyIsenable(true);
                        List<HBcolumnfamilyConfigDo> familySelectDos = familyMapper.selectSelective(familySelectModel);
                        List<HBcolumnfamilyConfigDto> familySelectDots = new ArrayList();
                        //xun-yu.she 20190725
                        // 一次获取一个表所有的familyid列表所有的column
                        List<String> hbcolumnfamilys = new ArrayList<>();
                        for (HBcolumnfamilyConfigDo familyModel : familySelectDos) {
                            hbcolumnfamilys.add(familyModel.getHbcolumnfamilyId());
                        }
                        if (hbcolumnfamilys.size() > 0) {
                            List<HBcolumnConfigDo> hBfamliyscolumnConfigDos = columnMapper.selectByhbcolumnfamilyIdList(hbcolumnfamilys);
                            for (HBcolumnfamilyConfigDo familyModel :
                                    familySelectDos) {
                                HBcolumnfamilyConfigDto hBcolumnfamilyConfigDto = new HBcolumnfamilyConfigDto();
                                hBcolumnfamilyConfigDto = familyModel.toData();
                               /* HBcolumnConfigDo hBcolumnConfigDo = new HBcolumnConfigDo();
                                hBcolumnConfigDo.setHbcolumnfamilyId(hBcolumnfamilyConfigDto.getHbcolumnfamilyId());
                                hBcolumnConfigDo.setHbcolumnIsenable(true);
                                List<HBcolumnConfigDo> hBcolumnConfigDos = columnMapper.selectSelective(hBcolumnConfigDo);*/
                                //从集合里面取，省去每一个Family就查数据库
                                List<HBcolumnConfigDo> hBcolumnConfigDos = hBfamliyscolumnConfigDos.stream().filter(x -> x.getHbcolumnfamilyId().equals(familyModel.getHbcolumnfamilyId())).collect(Collectors.toList());
                                List<HBcolumnConfigDto> hBcolumnConfigDtos = new ArrayList<>();
                                for (HBcolumnConfigDo columnModel :
                                        hBcolumnConfigDos) {
                                    //region SetModel
                                    hBcolumnConfigDtos.add(columnModel.toData());
                                }
                                hBcolumnfamilyConfigDto.setColumnConfigDtos(hBcolumnConfigDtos);
                                familySelectDots.add(hBcolumnfamilyConfigDto);
                                hBtableConfigDto.setColumnfamilyConfigDtos(familySelectDots);
                            }
                        }
                        hBtableConfigDtos.add(hBtableConfigDto);
                    }
                    session.commit();
                    return hBtableConfigDtos;
                }
                return null;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (
                Exception ex) {
            throw ex;
        }
    }

    private List<HbaseTablesInfoDto> getHbaseconfigTables(HBtableConfigDoMapper hBtableConfigDoMapper, TablelogDoMapper tablelogDoMapper, IndexlogDoMapper indexlogDoMapper, List<String> list) {
        List<HbaseTablesInfoDto> hbaseTablesInfoDtos = new ArrayList<>();
        List<HBtableConfigDo> hBtableConfigDos = hBtableConfigDoMapper.selectByPrimaryKeyList(list);
        for (HBtableConfigDo hBtableConfigDoFor :
                hBtableConfigDos) {
            HbaseTablesInfoDto hbaseTablesInfoDto = new HbaseTablesInfoDto();
            hbaseTablesInfoDto.setHbtableId(hBtableConfigDoFor.getHbtableId());
            hbaseTablesInfoDto.setHbtableName(hBtableConfigDoFor.getHbtableName());
            TablelogDo tablelogDo = new TablelogDo();
            tablelogDo.setHbtableId(hBtableConfigDoFor.getHbtableId());
            List<TablelogDo> tablelogDos = tablelogDoMapper.selectSelective(tablelogDo);
            HashMap<String, TableLogDto> tableLogDtoHashMap = new HashMap<String, TableLogDto>();
            for (TablelogDo tablelogDoFor :
                    tablelogDos) {
                tableLogDtoHashMap.put(tablelogDoFor.getTablelogId(), tablelogDoFor.toData());
            }
            hbaseTablesInfoDto.setHbtableLogName(tableLogDtoHashMap);
            IndexlogDo indexlogDo = new IndexlogDo();
            indexlogDo.setHbtableId(hBtableConfigDoFor.getHbtableId());
            List<IndexlogDo> indexlogDos = indexlogDoMapper.selectSelective(indexlogDo);
            HashMap<String, IndexlogDto> indexlogDtoHashMap = new HashMap<String, IndexlogDto>();
            for (IndexlogDo indexlogDoFor :
                    indexlogDos) {
                indexlogDtoHashMap.put(indexlogDoFor.getIndexlogId(), indexlogDoFor.toData());
            }
            hbaseTablesInfoDto.setHbtableIndexName(indexlogDtoHashMap);
            hbaseTablesInfoDtos.add(hbaseTablesInfoDto);
        }
        return hbaseTablesInfoDtos;

    }

    public List<HbaseTablesInfoDto> getHbaseconfigTablesSelect(List<String> list) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {

                HBtableConfigDoMapper hBtableConfigDoMapper = session.getMapper(HBtableConfigDoMapper.class);
                TablelogDoMapper tablelogDoMapper = session.getMapper(TablelogDoMapper.class);
                IndexlogDoMapper indexlogDoMapper = session.getMapper(IndexlogDoMapper.class);
                List<HbaseTablesInfoDto> hbaseTablesInfoDtos = getHbaseconfigTables(hBtableConfigDoMapper, tablelogDoMapper, indexlogDoMapper, list);
                return hbaseTablesInfoDtos;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<HbaseTablesInfoDto> getHbaseconfigTablesAccountid(List<String> list) throws Exception {
        try (SqlSession session = Dao.sqlSessionFactory.openSession()) {
            try {
                HBtableConfigDoMapper hBtableConfigDoMapper = session.getMapper(HBtableConfigDoMapper.class);
                TablelogDoMapper tablelogDoMapper = session.getMapper(TablelogDoMapper.class);
                IndexlogDoMapper indexlogDoMapper = session.getMapper(IndexlogDoMapper.class);
                List<HbaseTablesInfoDto> hbaseTablesInfoDtos = getHbaseconfigTables(hBtableConfigDoMapper, tablelogDoMapper, indexlogDoMapper, list);
                if (hbaseTablesInfoDtos != null && hbaseTablesInfoDtos.size() > 0) {
                    List<HBtableConfigDto> hBtableConfigDtos = selectByPrimaryKeyList(list);
                    for (HbaseTablesInfoDto hbaseTablesInfoDtoFor :
                            hbaseTablesInfoDtos) {
                        List<HBtableConfigDto> hBtableConfigDtoList = hBtableConfigDtos.stream().filter(x -> x.getHbtableId().equals(hbaseTablesInfoDtoFor.getHbtableId())).collect(Collectors.toList());
                        if (hBtableConfigDtoList != null && hBtableConfigDtoList.size() > 0) {

                            HBtableConfigDto hBtableConfigDto = hBtableConfigDtoList.get(0);
                            List<HBcolumnfamilyConfigDto> hBcolumnfamilyConfigDtos = hBtableConfigDto.getColumnfamilyConfigDtos();
                            if (hBcolumnfamilyConfigDtos != null && hBcolumnfamilyConfigDtos.size() > 0) {
                                HashMap<String, HBcolumnfamilyConfigDto> hBcolumnfamilyConfigDtoHashMap = new HashMap<String, HBcolumnfamilyConfigDto>();
                                for (HBcolumnfamilyConfigDto hBcolumnfamilyConfigDtoFor :
                                        hBcolumnfamilyConfigDtos) {
                                    hBcolumnfamilyConfigDtoHashMap.put(hBcolumnfamilyConfigDtoFor.getHbcolumnfamilyId(), hBcolumnfamilyConfigDtoFor);
                                }
                                hbaseTablesInfoDtoFor.setHbColumnFamilys(hBcolumnfamilyConfigDtoHashMap);

                            }
                        }
                    }
                }
                return hbaseTablesInfoDtos;
            } catch (Exception ex) {
                session.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public int updateInittag(String hbtableId) {
        try (SqlSession sqlSession = Dao.sqlSessionFactory.openSession()) {
            int i = sqlSession.getMapper(HBtableConfigDoMapper.class).updateInittag(hbtableId);

            sqlSession.commit();
            return i;
        }
    }

}
