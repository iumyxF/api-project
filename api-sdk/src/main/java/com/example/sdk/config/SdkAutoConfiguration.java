package com.example.sdk.config;

import com.example.sdk.config.properties.SdkProperties;
import com.example.sdk.service.IClientService;
import com.example.sdk.service.impl.ClientServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author feng
 * @date 2023/4/18 20:11
 */
@Configuration
@EnableConfigurationProperties({SdkProperties.class})
public class SdkAutoConfiguration {

    @Resource
    private SdkProperties sdkProperties;

    @Bean(name = "apiSdkClientService")
    @ConditionalOnMissingBean
    public IClientService clientService() {
        return new ClientServiceImpl(sdkProperties.getAddress(), sdkProperties.getPort());
    }
}
