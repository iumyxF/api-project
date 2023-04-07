package com.example.api.provider;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.api.common.ErrorCode;
import com.example.api.common.model.InnerResult;
import com.example.api.common.model.entity.UserInterfaceInfo;
import com.example.api.common.service.InnerUserInterfaceInfoService;
import com.example.api.exception.BusinessException;
import com.example.api.service.UserInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fzy
 * @description:
 * @Date 2023/4/7 10:35
 */
@Slf4j
@Service
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public InnerResult<Boolean> incrementInterfaceCallCount(long interfaceId, long userId) {
        //校验
        if (interfaceId <= 0 || userId <= 0) {
            log.error("请求参数异常");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.debug("接口 [id:{}] 被用户 [id:{}] 成功调用,调用次数加一", interfaceId, userId);
        UpdateWrapper<UserInterfaceInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("userId", userId);
        wrapper.eq("interfaceInfoId", interfaceId);
        wrapper.setSql("leftNum = leftNum - 1 , total = totalNum + 1");
        boolean update = userInterfaceInfoService.update(wrapper);
        return InnerResult.ok(update);
    }
}
