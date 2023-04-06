package com.example.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.common.ErrorCode;
import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.constant.SystemConstant;
import com.example.api.exception.BusinessException;
import com.example.api.mapper.InterfaceInfoMapper;
import com.example.api.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author iumyxF
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @date 2023-03-04 22:29:12
 */
@Slf4j
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * interfaceInfo 参数校验
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String params = interfaceInfo.getRequestParams();
        String requestHeader = interfaceInfo.getRequestHeader();
        String method = interfaceInfo.getMethod();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, url, requestHeader, method, params)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > SystemConstant.MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public InterfaceInfo selectInterfaceInfoByUrlAndMethod(String url, String method) {
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<InterfaceInfo>()
                .eq(InterfaceInfo::getUrl, url)
                .eq(InterfaceInfo::getMethod, method);
        return interfaceInfoMapper.selectOne(wrapper);
    }
}




