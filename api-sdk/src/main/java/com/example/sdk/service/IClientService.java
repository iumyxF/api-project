package com.example.sdk.service;

import java.util.Map;

/**
 * @author feng
 * @date 2023/4/18 20:15
 */
public interface IClientService {

    /**
     * GET请求
     *
     * @param requestParams 请求参数
     * @return 接口返回JSON字符串结果
     */
    String doGet(Map<String, Object> requestParams);

    /**
     * POST请求
     *
     * @param requestParams 请求参数
     * @return 接口返回JSON字符串结果
     */
    String doPost(Map<String, Object> requestParams);
}
