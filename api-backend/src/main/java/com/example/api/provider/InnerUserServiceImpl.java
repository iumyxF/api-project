package com.example.api.provider;

import com.example.api.common.model.InnerResult;
import com.example.api.common.model.entity.User;
import com.example.api.common.service.InnerUserService;
import com.example.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fzy
 * @description:
 * @Date 2023/4/8 9:18
 */
@Slf4j
@Service
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserService userService;

    @Override
    public InnerResult<User> selectUserByAccessKey(String accessKey) {
        User user = userService.selectUserByAccessKey(accessKey);
        log.info("根据 accessKey:{}, 查询得到的用户为:{}", accessKey, user);
        return InnerResult.ok(user);
    }
}
