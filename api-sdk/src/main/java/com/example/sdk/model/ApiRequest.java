package com.example.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author fzy
 * @description: 接口调用请求参数
 * @date 2023/4/19 9:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequest {

    /**
     * 请求方式
     */
    private String method;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 接口必要参数
     */
    private Map<String, Object> interfaceParams;

}
