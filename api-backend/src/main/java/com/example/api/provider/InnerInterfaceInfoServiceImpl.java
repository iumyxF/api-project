package com.example.api.provider;

import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.common.service.InnerInterfaceInfoService;
import com.example.api.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author iumyxF
 * @description:
 * @Date 2023/4/6 15:20
 */
@Slf4j
@Component
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    public InterfaceInfo selectInterfaceInfo(String url, String method) {
        InterfaceInfo interfaceInfo = interfaceInfoService.selectInterfaceInfoByUrlAndMethod(url, method);
        log.info("[dubbo] 远程查询接口的id: {}", interfaceInfo.getId());
        return interfaceInfo;
    }
}
