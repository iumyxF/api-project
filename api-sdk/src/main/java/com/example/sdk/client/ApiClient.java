package com.example.sdk.client;

import cn.hutool.extra.spring.SpringUtil;
import com.example.sdk.model.*;
import com.example.sdk.service.IClientService;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author fzy
 * @description: Api客户端
 * @date 2023/4/19 9:33
 */
public class ApiClient {

    /**
     * 凭证对象
     */
    private Credential credential;

    /**
     * 客户端配置信息
     */
    private ClientProfile clientProfile;

    /**
     * 发送请求实现类
     */
    private IClientService clientService;

    public ApiClient(Credential credential, ClientProfile clientProfile) {
        this.credential = credential;
        this.clientProfile = clientProfile;
        this.clientService = SpringUtil.getBean(IClientService.class);
    }


    public ApiResponse sendRequest(ApiRequest request) {
        String accessKey = credential.getAccessKey();
        String secretKey = credential.getSecretKey();
        if (StringUtils.isAnyBlank(accessKey, secretKey)) {
            return ApiResponse.fail("accessKey或secretKey不能为空");
        }
        Map<String, Object> interfaceParams = request.getInterfaceParams();
        if (interfaceParams.isEmpty()) {
            return ApiResponse.fail("接口参数缺失");
        }
        HttpProfile httpProfile = clientProfile.getHttpProfile();
        if (null == httpProfile && StringUtils.isBlank(httpProfile.getMethod())) {
            return ApiResponse.fail("请配置正确的HttpProfile");
        }

        //增加校验参数
        long currentTimeMillis = System.currentTimeMillis();

        String method = httpProfile.getMethod();
        if (HttpMethod.GET.matches(method)) {
            //clientService.doGet();
        } else {
            //clientService.doPost();
        }
        return ApiResponse.ok();
    }

}
