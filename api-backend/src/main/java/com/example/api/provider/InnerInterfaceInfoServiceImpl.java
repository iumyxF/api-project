package com.example.api.provider;

import com.example.api.common.model.InnerResult;
import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.common.service.InnerInterfaceInfoService;
import com.example.api.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

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
    public InnerResult<InterfaceInfo> selectInterfaceInfo(String url, String method) {
        InterfaceInfo interfaceInfo = interfaceInfoService.selectInterfaceInfoByUrlAndMethod(url, method);
        if (Objects.isNull(interfaceInfo)) {
            return InnerResult.fail();
        }
        log.info("[dubbo] 远程查询接口的id: {}", interfaceInfo.getId());
        return InnerResult.ok(interfaceInfo);
    }
}
