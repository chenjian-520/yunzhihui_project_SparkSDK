package com.tm.dl.javasdk.dpspark.streaming;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Description:  com.tm.dl.javasdk.dpspark.streaming
 * Copyright: © 2019 Foxconn. All rights reserved.
 * Company: Foxconn
 *
 * @author FL
 * @version 1.0
 * @timestamp 2019/11/14
 */
public interface SerializableConsumer<T> extends Consumer<T>, Serializable {
}
