package com.example.api.provider;

import com.example.api.common.service.DemoService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description: 测试Dubbo
 * @Date 2023/4/4 9:40
 * @Author fzy
 */
@DubboService
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "你好 " + name;
    }

}
