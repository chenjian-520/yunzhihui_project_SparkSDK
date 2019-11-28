package com.tm.dl.javasdk.dpspark.sqlserver;

import com.tm.dl.javasdk.dpspark.streaming.TestSender;

/**
 * Description:  com.tm.dl.javasdk.dpspark.sqlserver
 * Copyright: © 2019 Maxnerva. All rights reserved.
 * Company: IISD
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/12
 */
public class KafkaSendTest {
    public static void main(String[] args) {
        new TestSender().start();
    }
}
