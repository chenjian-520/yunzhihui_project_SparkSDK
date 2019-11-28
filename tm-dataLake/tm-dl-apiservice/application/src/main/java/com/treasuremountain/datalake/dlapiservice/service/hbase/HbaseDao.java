package com.treasuremountain.datalake.dlapiservice.service.hbase;

import com.treasuremountain.tmcommon.thirdpartyservice.hbase.TMDLHbOperator;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by gerryzhao on 10/20/2018.
 */
@Component
public class HbaseDao {
    public void init(String quorum, String clientPort) throws IOException {
        //每次连接hbase需要重新初始化以切换不同的用户
        TMDLHbOperator.init(quorum, clientPort);
    }
}
