package com.example.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fzy
 * @description: 接口调用请求参数
 * @date 2023/4/19 9:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequest {

    private String[] templateParamSet;

}
