package com.example.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fzy
 * @description: 客户端配置对象
 * @date 2023/4/19 9:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfile {

    /**
     * 签名方式 默认HmacSHA256
     */
    private String signMethod;

    /**
     * http配置
     */
    private HttpProfile httpProfile;

    public ClientProfile(HttpProfile httpProfile) {
        this.signMethod = "HmacSHA256";
        this.httpProfile = httpProfile;
    }
}
