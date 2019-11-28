package com.foxconn.tm.proxyserver.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/6/21.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Configuration
public class ToolsConfig {


    @Bean(name = "maxHttpClientUtils")
    public MaxHttpClientUtils maxHttpClientUtils(){
        MaxHttpClientUtils maxHttpClientUtils =new MaxHttpClientUtils();
        maxHttpClientUtils.init(60000, 60000, 60000, 2000, 1500);
        return maxHttpClientUtils;
    }

    @Bean(name = "maxHttpClientUtils3000")
    public MaxHttpClientUtils maxHttpClientUtils3000(){
        MaxHttpClientUtils maxHttpClientUtils =new MaxHttpClientUtils();
        maxHttpClientUtils.init(3000, 3000, 3000, 2000, 1500);
        return maxHttpClientUtils;
    }
}
