package com.example.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.model.entity.InterfaceInfo;

/**
 * @author feng
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-03-04 22:29:12
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

}
