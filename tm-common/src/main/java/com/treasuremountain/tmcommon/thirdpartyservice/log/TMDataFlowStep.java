package com.treasuremountain.tmcommon.thirdpartyservice.log;

/**
 * Created by gerryzhao on 11/4/2018.
 */
public enum TMDataFlowStep {

    DLMakeModelReceived,
    DLMakeModelCreated,
    DLInsertRestReceived,
    DLInsertSendToQuery,
    DLInsertSendToThirdParty,
    DLSearchRsetReceived,
    DLSearchRsetResulted,
    DLPersistenceReceived,
    DLPersistenceComplate,

    DLFileupload,

    MGkpReceived,
    MGkpSendToQuery,
    MGkpCloudReceived,
    MGkpCloudSendToDL,



    // kettel manager step code
    KETTLE_START, // kettle转换开始
    KETTLE_RUNNING, //kettle转化运行中
    KETTLE_END,// kettle转换结束
    KETTLE_STOP, // 停止kettle
    KETTLE_EXECUTE_CLUSTER,// kettle集群

    //fileimporter step code
    FILE_RECIVE_START,// 开始接收文件
    FILE_RECIVE_END,// 接收文件完成
    FILE_PUT_MQ, // 发送文件到消息队列
    FILE_GET_MQ,// 从消息队列接收文件
    FILE_PARSING_START,//开始读取文件
    FILE_PARSING_END,//读取文件结束
    FILE_CREAT_ROWKEY_START,//开始生成rowkey
    FILE_CREAT_ROWKEY_END,//结束生成rowkey
    FILE_MAPPING_START,// 开始处理mapping
    FILE_MAPPING_END,// 结束处理mapping
    FILE_SEND_START,// 开始发送数据湖
    FILE_SEND_END,// 结束发送数据湖
}
