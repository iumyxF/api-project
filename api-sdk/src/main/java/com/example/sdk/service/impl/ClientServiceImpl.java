package com.example.sdk.service.impl;

import cn.hutool.http.HttpUtil;
import com.example.sdk.service.IClientService;

import java.util.Map;

/**
 * @author feng
 * @date 2023/4/18 20:22
 */
public class ClientServiceImpl implements IClientService {
    /**
     * 网关ip地址
     */
    private final String address;

    /**
     * 网关端口
     */
    private final int port;


    public ClientServiceImpl(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public String doGet(Map<String, Object> requestParams) {
        String ip = address + ":" + port;
        return HttpUtil.get(ip, requestParams);
    }

    @Override
    public String doPost(Map<String, Object> requestParams) {
        String ip = address + ":" + port;
        return HttpUtil.post(ip, requestParams);
    }
}
