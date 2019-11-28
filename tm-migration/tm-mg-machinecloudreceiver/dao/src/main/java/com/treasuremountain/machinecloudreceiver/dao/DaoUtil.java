/**
* Copyright © 2016-2017 The Polarisboard Authors
 */
package com.treasuremountain.machinecloudreceiver.dao;



import com.treasuremountain.machinecloudreceiver.dao.model.ToData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class DaoUtil {

    private DaoUtil() {
    }

    public static <T> List<T> convertDataList(Collection<? extends ToData<T>> toDataList) {
        List<T> list = Collections.emptyList();
        if (toDataList != null && !toDataList.isEmpty()) {
            list = new ArrayList<>();
            for (ToData<T> object : toDataList) {
                list.add(object.toData());
            }
        }
        return list;
    }

    public static <T> T getData(ToData<T> data) {
        T object = null;
        if (data != null) {
            object = data.toData();
        }
        return object;
    }
}
