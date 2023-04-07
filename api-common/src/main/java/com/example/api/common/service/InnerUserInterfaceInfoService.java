package com.example.api.common.service;

import com.example.api.common.model.InnerResult;

/**
 * @author fzy
 * @description: 用户与接口关系服务
 * @Date 2023/4/7 10:01
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 递增接口调用次数
     *
     * @param interfaceId 接口id
     * @param userId      用户id
     * @return 结果
     */
    InnerResult<Boolean> incrementInterfaceCallCount(long interfaceId, long userId);
}
