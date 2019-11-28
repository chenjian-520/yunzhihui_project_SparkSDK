package com.treasuremountain.tmcommon.thirdpartyservice.log;

/**
 * Created by gerryzhao on 11/4/2018.
 */
public class TMlogMaker {

    /*
    {
	    "tmlog": {
            "businessId": "%s",
            "clientInfo": "%s",
            "msg": "%s",
            "step": "%s",
            "result": "%s",
            "isSuccess": "%d"
        }
    }

    直接字符串替换，序列化代价太大
    */

    public static String dataFlow(String businessId, String clientInfo, String msg, TMDataFlowStep step, String result, int isSuccess) {
        msg = msg.replace("\"", "'");
        String log = "{\"tmlog\": {\"businessId\": \"%s\",\"clientInfo\": \"%s\",\"msg\": \"%s\",\"step\": \"%s\",\"result\": \"%s\",\"isSuccess\": \"%d\"}}";
        return String.format(log, businessId, clientInfo, msg, step, result, isSuccess);
    }

    /*
     * kettlemanage 使用產生log數據
     * **/
    public static String dataFlowEtl(String businessId, String clientInfo, String msg, TMDataFlowStep step, String result, int isSuccess,String taskid,String logdatetime){
        msg = msg.replace("\"", "'");
        String log = "{\"tmlog\": {\"businessId\": \"%s\",\"clientInfo\": \"%s\",\"step\": \"%s\",\"result\": \"%s\",\"isSuccess\": \"%d\",\"taskid\": \"%s\",\"logdatetime\": \"%s\",\"msg\": \"%s\"}}";
        return String.format(log, businessId, clientInfo, step, result, isSuccess,taskid,logdatetime, msg);
    }
}
