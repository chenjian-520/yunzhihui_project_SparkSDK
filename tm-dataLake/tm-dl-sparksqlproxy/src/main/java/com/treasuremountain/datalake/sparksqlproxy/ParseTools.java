package com.treasuremountain.datalake.sparksqlproxy;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.ec.ECElGamalDecryptor;

/**
 * @Author:ref.tian
 * @Description:TODO
 * @timestamp:2019/9/18 12:40
 **/
@Slf4j
public class ParseTools {


    public static Long tryParseToLong(String invalue, Long defaultvalue) {
        try {
            return Long.parseLong(invalue.trim());
        } catch (Exception e) {
            log.error("转换异常（Long）：" + invalue);
            return defaultvalue;
        }
    }

    public static Double tryParseToDouble(String invalue, Double defaultvalue) {
        try {
            return Double.parseDouble(invalue.trim());
        } catch (Exception e) {
            log.error("转换异常（Double）：" + invalue);
            return defaultvalue;
        }
    }

}
