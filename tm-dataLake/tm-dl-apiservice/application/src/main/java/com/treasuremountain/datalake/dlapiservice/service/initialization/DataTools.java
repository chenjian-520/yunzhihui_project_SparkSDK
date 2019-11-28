package com.treasuremountain.datalake.dlapiservice.service.initialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/11/30.
 * Company: Foxconn
 * Project: MaxIoT
 */
public class DataTools {

    private static final ObjectMapper JSON = new ObjectMapper();

    public static ObjectNode newNode() {
        return JSON.createObjectNode();
    }

    public static byte[] toBytes(ObjectNode node) {
        return toString(node).getBytes(StandardCharsets.UTF_8);
    }

    public static JsonNode fromString(String data) {
        try {
            return JSON.readTree(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(JsonNode node) {
        try {
            return JSON.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toObjString(Object node) {
        try {
            return JSON.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查数据类型是否正确
     **/
    public static boolean checkDataType(String value, String type) {
        switch (type) {
            case "INT":
                return value.equals(String.valueOf(Integer.parseInt(value)));
            case "LONG":
                return value.equals(String.valueOf(Long.parseLong(value)));
            case "STRING":
                return true;
            case "FLOAT":
                String values = value.replace("f", "").replace("F", "");
                if (values.length() > values.indexOf(".") + 2) {
                    values = clearEndStr(values,"0");
                    values=StringUtils.endsWith(values,".")?values+"0":values;
                }
                return values.equals(String.valueOf(Float.parseFloat(value)));
            case "DOUBLE":
                values = value.replace("d", "").replace("D", "");
                if (values.length() > values.indexOf(".") + 2) {
                    values = clearEndStr(values,"0");
                    values=StringUtils.endsWith(values,".")?values+"0":values;
                }
                return values.equals(String.valueOf(Double.parseDouble(value)));
            default:
                return false;
        }

    }
    /**
     * 清理后缀
     * **/
    public static String clearEndStr(String value,String str){
        if(StringUtils.endsWith(value,str)){
            value = StringUtils.removeEnd(value,str);
            return clearEndStr(value,str);
        }else {
            return value;
        }
    }

}
