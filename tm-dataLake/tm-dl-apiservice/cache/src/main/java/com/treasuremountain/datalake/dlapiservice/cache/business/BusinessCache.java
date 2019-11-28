package com.treasuremountain.datalake.dlapiservice.cache.business;

import com.treasuremountain.datalake.dlapiservice.common.data.business.BusinessConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.data.htable.*;
import com.treasuremountain.datalake.dlapiservice.common.data.query.ExchangeConfigDto;
import com.treasuremountain.datalake.dlapiservice.common.data.twolevelindex.IndexlogDto;
import com.treasuremountain.datalake.dlapiservice.common.entity.business.*;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.business.BusinessImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.htable.*;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.kafka.KafkaImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.KafkaConfigDo;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.query.ExchangeImpl;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.twolevelindex.IndexlogImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by gerryzhao on 10/21/2018.
 * 增加加载业务对应的hhbase 表栏位的缓存 201911121117 ref.tian
 * 增加加载Kafka Topic配置的缓存 201911160820 Axin
 */
@Component
public class BusinessCache {

    private final static Logger log = LoggerFactory.getLogger(BusinessCache.class);

    public static java.util.Timer timer = new java.util.Timer(true);

    public volatile static List<BusinessEntity> businesList;

    public volatile static List<HBTableEntity> hbTableList;

    public volatile static List<TwoLevelIndexEntity> twoLevelIndexList;

    public volatile static List<TableSegmentEntity> tableSegmentList;
    //存储业务mapping的数据栏位类型信息  -ref.tian
    public volatile static ConcurrentHashMap<String,ConcurrentHashMap<String,RelationEntity>> businessColumnType;

    // kafkaconfig topic配置信息 -Axin
    public volatile static List<TopicEntity> topicList;

    public static void loadCache() {
        List<BusinessEntity> lbusinesList = new ArrayList<>();
        ConcurrentHashMap<String,ConcurrentHashMap<String,RelationEntity>> lbusinessColumnType= new ConcurrentHashMap<>();
        BusinessImpl business = new BusinessImpl();
        RelationtableImpl relationtable = new RelationtableImpl();
        HBtableImpl hBtable = new HBtableImpl();
        ExchangeImpl exchange = new ExchangeImpl();

        List<BusinessConfigDto> businessConfigDtoList = business.findAllBusiness();
        businessConfigDtoList.forEach(d -> {
            try {
                String businessId = d.getBusinessId();
                String exchangeId = d.getExchangeId();
                ConcurrentHashMap<String,RelationEntity> concurrentHashMap = new ConcurrentHashMap<>();//当前业务mapping栏位的数据类型
                log.info("load business cache=====" + businessId);
                ExchangeEntity exchangeEntity = new ExchangeEntity();

                if (StringUtils.isNotBlank(exchangeId)) {
                    ExchangeConfigDto exchangeConfigDto = exchange.selectByPrimaryKey(exchangeId);
                    exchangeEntity.setExchangeId(exchangeConfigDto.getExchangeId());
                    exchangeEntity.setExchangeName(exchangeConfigDto.getExchangeName());
                }

                BusinessEntity b = new BusinessEntity();
                b.setBusinessId(businessId);
                b.setBusinessName(d.getBusinessName());
                b.setBusinessDesc(d.getBusinessDesc());
                b.setExchange(exchangeEntity);

                List<RelationtableConfigDto> relationtableConfigDtoList = relationtable.selectByBusinessId(businessId);
                List<RelationEntity> relationEntities = new ArrayList<>();
                relationtableConfigDtoList.forEach(m -> {

                    RelationtableConfigDto relationtableConfigDto = m;
                    RelationEntity relationEntity = new RelationEntity();
                    relationEntity.setRelationtableId(relationtableConfigDto.getRelationtableId());
                    relationEntity.setMsgkey(relationtableConfigDto.getMsgkey());

                    HBtableConfigDto hBtableConfigDto = hBtable.selectByPrimaryKey(relationtableConfigDto.getHbtableId());
                    HBTableEntity hbTableEntity = new HBTableEntity();
                    hbTableEntity.setHbtableId(hBtableConfigDto.getHbtableId());
                    hbTableEntity.setHbtableName(hBtableConfigDto.getHbtableName());
                    hbTableEntity.setHbtableIscompression(hBtableConfigDto.getHbtableIscompression());
                    hbTableEntity.setHbtableCompressionname(hBtableConfigDto.getHbtableCompressionname());
                    hbTableEntity.setHbtableIssplit(hBtableConfigDto.getHbtableIssplit());
                    hbTableEntity.setHbtableSplitinfo(hBtableConfigDto.getHbtableSplitinfo());
                    hbTableEntity.setHbtableDesc(hBtableConfigDto.getHbtableDesc());
                    hbTableEntity.setHbtableIstwoLevelIndex(hBtableConfigDto.getHbtableIstwoLevelIndex());

                    List<HBcolumnfamilyEntity> hBcolumnfamilyEntityList = makehbColumnfamilyList(hBtableConfigDto.getHbtableId());

                    hbTableEntity.setColumnfamilyList(hBcolumnfamilyEntityList);

                    relationEntity.setHbtable(hbTableEntity);
                    hbTableEntity.getColumnfamilyList().stream().filter(g -> g.getHbcolumnfamilyId().equals(relationtableConfigDto.getHbcolumnfamilyId())).findFirst().map(h -> {
                        relationEntity.setHbcolumnfamilyName(h.getHbcolumnfamilyName());
                        h.getColumnList().stream().filter(k -> k.getHbcolumnId().equals(relationtableConfigDto.getHbcolumnId())).findFirst().map(l -> {
                            relationEntity.setHbcolumnName(l.getHbcolumnName());
                            relationEntity.setHbcolumnType(l.getHbcolumnType());
                            return true;
                        });
                        return true;
                    });
                    concurrentHashMap.putIfAbsent(relationEntity.getMsgkey(),relationEntity);
                    relationEntities.add(relationEntity);
                });

                b.setRelationList(relationEntities);
                lbusinesList.add(b);
                lbusinessColumnType.putIfAbsent(businessId,concurrentHashMap);
            } catch (Exception e) {
                log.error("加载业务配置信息异常", e);
            }
        });

        businesList = lbusinesList;
        businessColumnType = lbusinessColumnType;
    }

    /**
     * 只加载特定id的业务信息
     ***/
    public static void loadCacheByid(String businessid) {
        BusinessImpl business = new BusinessImpl();
        RelationtableImpl relationtable = new RelationtableImpl();
        HBtableImpl hBtable = new HBtableImpl();
        ExchangeImpl exchange = new ExchangeImpl();
        try {
            BusinessConfigDto businessConfigDto = business.selectByPrimaryKey(businessid);
            if (businessConfigDto == null) {
                // todo ref.tian
            }
            String businessId = businessConfigDto.getBusinessId();
            String exchangeId = businessConfigDto.getExchangeId();
            log.info("load business cache=====" + businessId);
            ExchangeEntity exchangeEntity = new ExchangeEntity();

            if (StringUtils.isNotBlank(exchangeId)) {
                ExchangeConfigDto exchangeConfigDto = exchange.selectByPrimaryKey(exchangeId);
                exchangeEntity.setExchangeId(exchangeConfigDto.getExchangeId());
                exchangeEntity.setExchangeName(exchangeConfigDto.getExchangeName());
            }

            BusinessEntity b = new BusinessEntity();
            b.setBusinessId(businessId);
            b.setBusinessName(businessConfigDto.getBusinessName());
            b.setBusinessDesc(businessConfigDto.getBusinessDesc());
            b.setExchange(exchangeEntity);

            List<RelationtableConfigDto> relationtableConfigDtoList = relationtable.selectByBusinessId(businessId);
            List<RelationEntity> relationEntities = new ArrayList<>();
            relationtableConfigDtoList.forEach(m -> {

                RelationtableConfigDto relationtableConfigDto = m;
                RelationEntity relationEntity = new RelationEntity();
                relationEntity.setRelationtableId(relationtableConfigDto.getRelationtableId());
                relationEntity.setMsgkey(relationtableConfigDto.getMsgkey());

                HBtableConfigDto hBtableConfigDto = hBtable.selectByPrimaryKey(relationtableConfigDto.getHbtableId());
                HBTableEntity hbTableEntity = new HBTableEntity();
                hbTableEntity.setHbtableId(hBtableConfigDto.getHbtableId());
                hbTableEntity.setHbtableName(hBtableConfigDto.getHbtableName());
                hbTableEntity.setHbtableIscompression(hBtableConfigDto.getHbtableIscompression());
                hbTableEntity.setHbtableCompressionname(hBtableConfigDto.getHbtableCompressionname());
                hbTableEntity.setHbtableIssplit(hBtableConfigDto.getHbtableIssplit());
                hbTableEntity.setHbtableSplitinfo(hBtableConfigDto.getHbtableSplitinfo());
                hbTableEntity.setHbtableDesc(hBtableConfigDto.getHbtableDesc());
                hbTableEntity.setHbtableIstwoLevelIndex(hBtableConfigDto.getHbtableIstwoLevelIndex());

                List<HBcolumnfamilyEntity> hBcolumnfamilyEntityList = makehbColumnfamilyList(hBtableConfigDto.getHbtableId());

                hbTableEntity.setColumnfamilyList(hBcolumnfamilyEntityList);

                relationEntity.setHbtable(hbTableEntity);
                hbTableEntity.getColumnfamilyList().stream().filter(g -> g.getHbcolumnfamilyId().equals(relationtableConfigDto.getHbcolumnfamilyId())).findFirst().map(h -> {
                    relationEntity.setHbcolumnfamilyName(h.getHbcolumnfamilyName());
                    h.getColumnList().stream().filter(k -> k.getHbcolumnId().equals(relationtableConfigDto.getHbcolumnId())).findFirst().map(l -> {
                        relationEntity.setHbcolumnName(l.getHbcolumnName());
                        return true;
                    });
                    return true;
                });

                relationEntities.add(relationEntity);
            });

            b.setRelationList(relationEntities);
            if (!businesList.contains(b)) {
                businesList.add(b);
            } else {
                businesList.remove(b);
                businesList.add(b);
            }
        } catch (Exception e) {
            log.error("加载业务配置信息异常", e);
        }
    }

    /**
     * 只删除特定id的业务信息缓存
     ***/
    public static void removeCacheByid(String businessid) {
        BusinessImpl business = new BusinessImpl();
        BusinessEntity businessEntity = new BusinessEntity();
        try {
            businessEntity.setBusinessId(businessid);
            businesList.remove(business);
        } catch (Exception e) {
            log.error("删除业务配置信息异常", e);
        }
    }

    public static void loadHtableInfo() {
        List<HBTableEntity> lhbTableList = new ArrayList<>();

        HBtableImpl hBtable = new HBtableImpl();
        List<HBtableConfigDto> hBtableConfigDtos = hBtable.selectAll();

        hBtableConfigDtos.forEach(d -> {
            try {
                HBTableEntity hbTableEntity = new HBTableEntity();

                String tableId = d.getHbtableId();

                hbTableEntity.setHbtableId(tableId);
                hbTableEntity.setHbtableName(d.getHbtableName());
                hbTableEntity.setHbtableIscompression(d.getHbtableIscompression());
                hbTableEntity.setHbtableCompressionname(d.getHbtableCompressionname());
                hbTableEntity.setHbtableIssplit(d.getHbtableIssplit());
                hbTableEntity.setHbtableSplitinfo(d.getHbtableSplitinfo());
                hbTableEntity.setHbtableDesc(d.getHbtableDesc());
                hbTableEntity.setHbtableIstwoLevelIndex(d.getHbtableIstwoLevelIndex());

                List<HBcolumnfamilyEntity> hBcolumnfamilyEntityList = makehbColumnfamilyList(tableId);

                hbTableEntity.setColumnfamilyList(hBcolumnfamilyEntityList);

                lhbTableList.add(hbTableEntity);
            } catch (Exception e) {
                log.error("加载Htable 缓存异常", e);
            }
        });

        hbTableList = lhbTableList;
    }

    /***
     * 只加载特定id的htable数据到缓存中
     * ***/
    public static void loadHtableInfoByid(String htableid) {
        HBtableImpl hBtable = new HBtableImpl();
        HBtableConfigDto btableConfigDto = hBtable.selectByPrimaryKey(htableid);
        try {
            HBTableEntity hbTableEntity = new HBTableEntity();
            String tableId = btableConfigDto.getHbtableId();
            hbTableEntity.setHbtableId(tableId);
            hbTableEntity.setHbtableName(btableConfigDto.getHbtableName());
            hbTableEntity.setHbtableIscompression(btableConfigDto.getHbtableIscompression());
            hbTableEntity.setHbtableCompressionname(btableConfigDto.getHbtableCompressionname());
            hbTableEntity.setHbtableIssplit(btableConfigDto.getHbtableIssplit());
            hbTableEntity.setHbtableSplitinfo(btableConfigDto.getHbtableSplitinfo());
            hbTableEntity.setHbtableDesc(btableConfigDto.getHbtableDesc());
            hbTableEntity.setHbtableIstwoLevelIndex(btableConfigDto.getHbtableIstwoLevelIndex());
            List<HBcolumnfamilyEntity> hBcolumnfamilyEntityList = makehbColumnfamilyList(tableId);

            hbTableEntity.setColumnfamilyList(hBcolumnfamilyEntityList);
            if (!hbTableList.contains(hbTableEntity)) {
                hbTableList.add(hbTableEntity);
            } else {
                hbTableList.remove(hbTableEntity);
                hbTableList.add(hbTableEntity);
            }
        } catch (Exception e) {
            log.error("加载Htable 缓存异常", e);
        }
    }

    /***
     * 只删除特定id的htable数据到缓存中
     * ***/
    public static void removeHtableInfoByid(String htableid) {
        try {
            HBTableEntity hbTableEntity = new HBTableEntity();
            hbTableEntity.setHbtableId(htableid);
            hbTableList.remove(hbTableEntity);
        } catch (Exception e) {
            log.error("删除Htable 缓存异常", e);
        }
    }


    public static void loadTwoLevelIndexConfig() {
        List<TwoLevelIndexEntity> indexList = new ArrayList<>();

        HBtableImpl hBtable = new HBtableImpl();
        List<HBtableConfigDto> hBtableConfigDtos = hBtable.selectAll();

        IndexlogImpl indexlog = new IndexlogImpl();
        List<IndexlogDto> indexlogDtos = indexlog.selectAll();

        hBtableConfigDtos.forEach(d -> {
            String hbid = d.getHbtableId();
            try {
                HtableIndexEntity hbe = new HtableIndexEntity();
                hbe.setHbtableId(hbid);
                hbe.setHbtableIstwoLevelIndex(d.getHbtableIstwoLevelIndex());
                hbe.setHbsegmenttime(d.getHbindexsegmenttime());
                hbe.setHbretentiontime(d.getHbindexretentiontime());
                hbe.setHbcurrentindexname(d.getHbcurrentindexname());

                List<IndexlogEntity> indexlogList = new ArrayList<>();

                indexlogDtos.stream().filter(e -> e.getHbtableId().equals(hbid)).forEach(f -> {
                    IndexlogEntity ine = new IndexlogEntity();
                    ine.setHbtableId(hbid);
                    ine.setIndexlogId(f.getIndexlogId());
                    ine.setIndexlogName(f.getIndexlogName());
                    ine.setIndexlogCreatetime(f.getIndexlogCreatetime().getTime());

                    indexlogList.add(ine);
                });

                TwoLevelIndexEntity tie = new TwoLevelIndexEntity();
                tie.setHtName(d.getHbtableName());
                tie.setHtid(d.getHbtableId());
                tie.setHtableIndex(hbe);
                tie.setIndexlogList(indexlogList);

                indexList.add(tie);
            } catch (Exception e) {
                log.error("加载二级索引发现异常：" + hbid, e);
            }
        });

        twoLevelIndexList = indexList;
    }

    /**
     * 加载特定表的索引的配置信息
     ***/
    public static void loadTwoLevelIndexConfigByHtableid(String htableid) {
        HBtableImpl hBtable = new HBtableImpl();
        HBtableConfigDto btableConfigDto = hBtable.selectByPrimaryKey(htableid);
        if (btableConfigDto == null) {
            log.error("没有找到该htable 的数据(Index)：" + htableid);
            return;
        }
        IndexlogImpl indexlog = new IndexlogImpl();
        List<IndexlogDto> indexlogDtos = indexlog.selectAll();
        String hbid = btableConfigDto.getHbtableId();
        try {
            HtableIndexEntity hbe = new HtableIndexEntity();
            hbe.setHbtableId(hbid);
            hbe.setHbtableIstwoLevelIndex(btableConfigDto.getHbtableIstwoLevelIndex());
            hbe.setHbsegmenttime(btableConfigDto.getHbindexsegmenttime());
            hbe.setHbretentiontime(btableConfigDto.getHbindexretentiontime());
            hbe.setHbcurrentindexname(btableConfigDto.getHbcurrentindexname());

            List<IndexlogEntity> indexlogList = new ArrayList<>();

            indexlogDtos.stream().filter(e -> e.getHbtableId().equals(hbid)).forEach(f -> {
                IndexlogEntity ine = new IndexlogEntity();
                ine.setHbtableId(hbid);
                ine.setIndexlogId(f.getIndexlogId());
                ine.setIndexlogName(f.getIndexlogName());
                ine.setIndexlogCreatetime(f.getIndexlogCreatetime().getTime());

                indexlogList.add(ine);
            });

            TwoLevelIndexEntity tie = new TwoLevelIndexEntity();
            tie.setHtName(btableConfigDto.getHbtableName());
            tie.setHtid(btableConfigDto.getHbtableId());
            tie.setHtableIndex(hbe);
            tie.setIndexlogList(indexlogList);

            if (!twoLevelIndexList.contains(tie)) {
                twoLevelIndexList.add(tie);
            } else {
                twoLevelIndexList.remove(tie);
                twoLevelIndexList.add(tie);
            }
        } catch (Exception e) {
            log.error("加载二级索引发现异常：" + hbid, e);
        }
    }

    /**
     * 删除特定表的索引的配置信息
     ***/
    public static void removeTwoLevelIndexConfigByHtableid(String htableid) {

        IndexlogImpl indexlog = new IndexlogImpl();
        List<IndexlogDto> indexlogDtos = indexlog.selectAll();

        try {
            TwoLevelIndexEntity tie = new TwoLevelIndexEntity();
            tie.setHtid(htableid);
            twoLevelIndexList.remove(tie);
        } catch (Exception e) {
            log.error("删除二级索引发现异常", e);
        }
    }

    public static void loadTableSegmentConf() {
        List<TableSegmentEntity> segmentList = new ArrayList<>();

        HBtableImpl hBtable = new HBtableImpl();
        List<HBtableConfigDto> hBtableConfigDtos = hBtable.selectAll();

        TableLogImpl tableLog = new TableLogImpl();
        List<TableLogDto> tableLogDtos = tableLog.selectAll();

        hBtableConfigDtos.forEach(d -> {
            try {
                String htid = d.getHbtableId();
                String htName = d.getHbtableName();

                TableSegmentEntity segmentEntity = new TableSegmentEntity();
                segmentEntity.setHtid(htid);
                segmentEntity.setHtName(htName);

                HtableSegmentEntity hts = new HtableSegmentEntity();
                hts.setHbtableId(htid);
                hts.setHbtableIstablesegment(d.getHbtableIstablesegment());
                hts.setHbtablesegmenttime(d.getHbtablesegmenttime());
                hts.setHbtableretentiontime(d.getHbtableretentiontime());
                hts.setHbcurrenttablename(d.getHbcurrenttablename());

                segmentEntity.setHtableSegment(hts);

                List<TableLogEntity> tlelist = new ArrayList<>();

                tableLogDtos.stream().filter(e -> e.getHbtableId().equals(htid)).forEach(f -> {
                    TableLogEntity tle = new TableLogEntity();
                    tle.setTablelogId(f.getTablelogId());
                    tle.setHbtableId(f.getHbtableId());
                    tle.setTablelogName(f.getTablelogName());
                    tle.setTablelogCreatetime(f.getTablelogCreatetime().getTime());

                    tlelist.add(tle);
                });

                segmentEntity.setTableLogList(tlelist);
                segmentList.add(segmentEntity);
            } catch (Exception e) {
                log.error("加载TableSegment配置信息异常", e);
            }
        });
        tableSegmentList = segmentList;
    }

    /**
     * 根据htable  id查询表切片信息
     ***/
    public static void loadTableSegmentConfByHtableid(String htableid) {
        HBtableImpl hBtable = new HBtableImpl();
        HBtableConfigDto hBtableConfigDto = hBtable.selectByPrimaryKey(htableid);
        if (hBtableConfigDto == null) {
            log.error("没有找到该htable 的数据(Segment)：" + htableid);
            return;
        }
        TableLogImpl tableLog = new TableLogImpl();
        List<TableLogDto> tableLogDtos = tableLog.selectAll();

        try {
            String htid = hBtableConfigDto.getHbtableId();
            String htName = hBtableConfigDto.getHbtableName();

            TableSegmentEntity segmentEntity = new TableSegmentEntity();
            segmentEntity.setHtid(htid);
            segmentEntity.setHtName(htName);

            HtableSegmentEntity hts = new HtableSegmentEntity();
            hts.setHbtableId(htid);
            hts.setHbtableIstablesegment(hBtableConfigDto.getHbtableIstablesegment());
            hts.setHbtablesegmenttime(hBtableConfigDto.getHbtablesegmenttime());
            hts.setHbtableretentiontime(hBtableConfigDto.getHbtableretentiontime());
            hts.setHbcurrenttablename(hBtableConfigDto.getHbcurrenttablename());

            segmentEntity.setHtableSegment(hts);

            List<TableLogEntity> tlelist = new ArrayList<>();

            tableLogDtos.stream().filter(e -> e.getHbtableId().equals(htid)).forEach(f -> {
                TableLogEntity tle = new TableLogEntity();
                tle.setTablelogId(f.getTablelogId());
                tle.setHbtableId(f.getHbtableId());
                tle.setTablelogName(f.getTablelogName());
                tle.setTablelogCreatetime(f.getTablelogCreatetime().getTime());

                tlelist.add(tle);
            });

            segmentEntity.setTableLogList(tlelist);
            if (!tableSegmentList.contains(segmentEntity)) {
                tableSegmentList.add(segmentEntity);
            } else {
                tableSegmentList.remove(segmentEntity);
                tableSegmentList.add(segmentEntity);
            }
        } catch (Exception e) {
            log.error("加载TableSegment配置信息异常", e);
        }
    }

    /**
     * 根据htable  id删除表切片信息
     ***/
    public static void removeTableSegmentConfByHtableid(String htableid) {
        TableLogImpl tableLog = new TableLogImpl();
        List<TableLogDto> tableLogDtos = tableLog.selectAll();
        try {
            TableSegmentEntity segmentEntity = new TableSegmentEntity();
            segmentEntity.setHtid(htableid);
            tableSegmentList.remove(segmentEntity);
        } catch (Exception e) {
            log.error("删除TableSegment配置信息异常", e);
        }
    }

    private static List<HBcolumnfamilyEntity> makehbColumnfamilyList(String tableId) {

        HBcolumnfamilyImpl hBcolumnfamily = new HBcolumnfamilyImpl();
        HBcolumnImpl hBcolumn = new HBcolumnImpl();

        List<HBcolumnfamilyConfigDto> hBcolumnfamilyConfigDtoList = hBcolumnfamily.selectByhbtableId(tableId);
        List<HBcolumnfamilyEntity> hBcolumnfamilyEntityList = new ArrayList<>();
        hBcolumnfamilyConfigDtoList.forEach(e -> {
            HBcolumnfamilyEntity hBcolumnfamilyEntity = new HBcolumnfamilyEntity();
            hBcolumnfamilyEntity.setHbcolumnfamilyId(e.getHbcolumnfamilyId());
            hBcolumnfamilyEntity.setHbcolumnfamilyName(e.getHbcolumnfamilyName());

            List<HBcolumnConfigDto> hBcolumnConfigDtoList = hBcolumn.selectByhbcolumnfamilyId(e.getHbcolumnfamilyId());
            List<HBcolumnEntity> hBcolumnEntityList = new ArrayList<>();
            hBcolumnConfigDtoList.forEach(f -> {
                HBcolumnEntity hBcolumnEntity = new HBcolumnEntity();
                hBcolumnEntity.setHbcolumnId(f.getHbcolumnId());
                hBcolumnEntity.setHbcolumnfamilyId(f.getHbcolumnfamilyId());
                hBcolumnEntity.setHbcolumnName(f.getHbcolumnName());
                hBcolumnEntity.setHbcolumnIsindex(f.getHbcolumnIsindex());
                hBcolumnEntity.setHbcolumnType(f.getHbcolumnType());
                hBcolumnEntityList.add(hBcolumnEntity);
            });

            hBcolumnfamilyEntity.setColumnList(hBcolumnEntityList);
            hBcolumnfamilyEntityList.add(hBcolumnfamilyEntity);
        });

        return hBcolumnfamilyEntityList;
    }

    public static void loadKafkaConf(){
        List<TopicEntity> topicEntities = new ArrayList<>();

        KafkaImpl kafka = new KafkaImpl();
        List<KafkaConfigDo> kafkaConfigDos = kafka.selectAll();

        kafkaConfigDos.forEach(d->{
            TopicEntity topicEntity = new TopicEntity();
            topicEntity.setTopicName(d.getTopic());
            topicEntity.setTopicId(d.getId());
            topicEntity.setGroupId(d.getGroupId());
            topicEntities.add(topicEntity);
        });

        topicList = topicEntities;
    }

    public static void autoRefreshCache() {
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    log.error("== 启动加载 ==");
                    loadCache();
                    loadHtableInfo();
                    loadTwoLevelIndexConfig();
                    loadTableSegmentConf();
                    loadKafkaConf();
                    log.error("== 启动加载结束 ==");
                } catch (Exception ex) {
                    log.error("定时加载数据异常：" + ex.toString());
                }
            }
        };
        timer.schedule(task, 0, 10000);
    }

}
