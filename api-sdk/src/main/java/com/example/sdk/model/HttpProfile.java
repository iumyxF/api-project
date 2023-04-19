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

    private String method;

    private Long connTimeout;

    private String endpoint;
}
