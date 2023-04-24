package com.example.sdk.client;

import cn.hutool.http.HttpUtil;
import com.example.sdk.exception.SdkException;
import com.example.sdk.model.*;
import com.example.sdk.utils.SignUtils;
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

    private static final String GATEWAY_HOST = "127.0.0.1:8082";

    public ApiClient(Credential credential) {
        this.credential = credential;
    }

    public ApiResponse sendRequest(ApiRequest request) {
        String accessKey = credential.getAccessKey();
        String secretKey = credential.getSecretKey();
        if (StringUtils.isAnyBlank(accessKey, secretKey)) {
            throw new SdkException(ErrorCode.PARAMS_ERROR, "Credential参数不合法");
        }
        Map<String, Object> interfaceParams = request.getInterfaceParams();
        String method = request.getMethod();
        if (null == HttpMethod.resolve(method)) {
            throw new SdkException(ErrorCode.PARAMS_ERROR, "请求类型不合法");
        }
        if (StringUtils.isBlank(request.getUrl())) {
            throw new SdkException(ErrorCode.PARAMS_ERROR, "请求路径参数不合法");
        }
        if (interfaceParams.isEmpty()) {
            throw new SdkException(ErrorCode.PARAMS_ERROR, "请求参数不合法");
        }
        interfaceParams.put("accessKey", accessKey);
        interfaceParams.put("secretKey", secretKey);
        String pathParams = SignUtils.generateGetRequestParams(interfaceParams);
        String path = request.getUrl() + pathParams;
        if (HttpMethod.GET.matches(method)) {
            //TODO response 是什么？
            String response = HttpUtil.get(GATEWAY_HOST + path);
        } else {
            //clientService.doPost();
        }
        return ApiResponse.ok();
    }

}
