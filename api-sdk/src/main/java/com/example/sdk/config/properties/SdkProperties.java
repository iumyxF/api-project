package com.example.sdk.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author feng
 * @date 2023/4/18 20:12
 */
@ConfigurationProperties(prefix = "api.sdk.gateway")
public class SdkProperties {

    /**
     * 网关地址
     */
    private String address;

    /**
     * 网关端口
     */
    private int port;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
