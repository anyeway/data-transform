package com.nd.resource.transform.config;

import com.nd.resource.transform.repositroy.cs.MyConfig;
import com.nd.sdp.cs.common.CsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by way on 2016/8/30.
 */
@Configuration
public class SourceConfig {


    @Value("${cs.host}")
    private String host;

    @Value("${network.system}")
    private Integer network;

    @Bean
    public MyConfig csConfig(){
        CsConfig.setHost(host);
        MyConfig myCsConfig = new MyConfig();
        myCsConfig.setNetworkSystem(network);
        return myCsConfig;
    }
}
