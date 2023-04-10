package com.example.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.common.ErrorCode;
import com.example.api.common.model.entity.UserInterfaceInfo;
import com.example.api.exception.BusinessException;
import com.example.api.service.UserInterfaceInfoService;
import com.example.api.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author gonsin
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @date 2023-04-07 10:05:16
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo> implements UserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    /**
     * 新增次数时的校验
     *
     * @param userInterfaceInfo the user interface info
     * @param add               the add
     */
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }

    /**
     * 判断用户是否能调用远程接口
     *
     * @param userId      用户ID
     * @param interfaceId 接口ID
     * @return 结果
     */
    @Override
    public boolean verifyInvokeUserInterfaceInfo(Long userId, Long interfaceId) {
        LambdaQueryWrapper<UserInterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterfaceInfo::getUserId, userId);
        wrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectOne(wrapper);
        return null != userInterfaceInfo && userInterfaceInfo.getLeftNum() > 0 && userInterfaceInfo.getStatus() != 1;
    }
}




