package com.example.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.common.model.entity.InterfaceInfo;

/**
 * The interface info service.
 *
 * @author iumyxF
 * @description 针对表 【interface_info(接口信息)】的数据库操作Service
 * @date 2023 -03-04 22:29:12
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * Valid interface info.
     *
     * @param interfaceInfo the interface info
     * @param add           the add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 根据接口路径和请求方式查询接口信息
     *
     * @param url    接口路径
     * @param method 请求方式
     * @return 接口对象
     */
    InterfaceInfo selectInterfaceInfoByUrlAndMethod(String url, String method);
}
