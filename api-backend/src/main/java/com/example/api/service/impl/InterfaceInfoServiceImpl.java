package com.example.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.common.ErrorCode;
import com.example.api.exception.BusinessException;
import com.example.api.mapper.InterfaceInfoMapper;
import com.example.api.model.entity.InterfaceInfo;
import com.example.api.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author iumyxF
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @date  2023-03-04 22:29:12
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    /**
     * interfaceInfo 参数校验
     * @param interfaceInfo
     * @param add
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String method = interfaceInfo.getMethod();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, url, requestHeader, method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() < 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }
}




