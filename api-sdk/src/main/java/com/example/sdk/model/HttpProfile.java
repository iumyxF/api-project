package com.example.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fzy
 * @description: http配置对象
 * @date 2023/4/19 9:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpProfile {

    /**
     * 请求的接口的请求方式
     */
    private String method;

    /**
     * 超时时间 单位秒
     */
    private Long connTimeout;

    public HttpProfile(String method) {
        this.method = method;
        this.connTimeout = 1000L * 60;
    }
}
