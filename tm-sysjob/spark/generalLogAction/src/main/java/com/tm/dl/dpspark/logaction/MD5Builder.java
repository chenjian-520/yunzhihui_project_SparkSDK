package com.tm.dl.dpspark.logaction;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description:  com.maxiot.maxinsight.utils
 * Copyright: © 2017 FanLei. All rights reserved.
 * Company: NULL
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/7/16
 */
public class MD5Builder {
    private MD5Builder() {
    }

    private static String encryptString(String strSrc, String enc) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bt = strSrc.getBytes();
        MessageDigest md = MessageDigest.getInstance(enc);
        md.update(bt);
        String strDes = bytes2Hex(md.digest());
        return strDes;
    }

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;

        for (int i = 0; i < bts.length; ++i) {
            tmp = Integer.toHexString(bts[i] & 255);
            if (tmp.length() == 1) {
                des = des + "0";
            }

            des = des + tmp;
        }

        return des;
    }

    public static String getMD5String(String str) throws Exception {
        return encryptString(str, "MD5");
    }

    public static String getMD516String(String str) throws Exception {
        return encryptString(str, "MD5").substring(8, 24);
    }
}
