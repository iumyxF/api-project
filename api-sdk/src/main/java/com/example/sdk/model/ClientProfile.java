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

    private String signMethod;

    private HttpProfile httpProfile;
}
