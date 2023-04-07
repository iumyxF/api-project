package com.example.api.common.service;

import com.example.api.common.model.InnerResult;
import com.example.api.common.model.entity.InterfaceInfo;

/**
 * @description: interfaceInfo inner service
 * @Date 2023/4/6 15:03
 * @Author fzy
 */
public interface InnerInterfaceInfoService {

    /**
     * 根据接口路径和请求方式查询接口信息
     *
     * @param url    接口路径
     * @param method 请求方式
     * @return 接口对象
     */
    InnerResult<InterfaceInfo> selectInterfaceInfo(String url, String method);
}
