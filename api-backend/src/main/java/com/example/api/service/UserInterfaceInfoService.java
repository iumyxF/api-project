package com.example.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.common.model.entity.UserInterfaceInfo;

/**
 * The interface User interface info service.
 *
 * @author gonsin
 * @description 针对表 【user_interface_info(用户调用接口关系)】的数据库操作Service
 * @date  2023 -04-07 10:05:16
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * Valid user interface info.
     *
     * @param userInterfaceInfo the user interface info
     * @param add               the add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    boolean verifyInvokeUserInterfaceInfo(Long userId, Long interfaceId);
}
