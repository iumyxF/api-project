package com.example.sdk.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.example.sdk.exception.SdkException;
import com.example.sdk.model.*;
import com.example.sdk.utils.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author fzy
 * @description: Api客户端
 * @date 2023/4/19 9:33
 */
public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    /**
     * 凭证对象
     */
    private final Credential credential;

    private static final String GATEWAY_HOST = "127.0.0.1:8082";

    public ApiClient(Credential credential) {
        this.credential = credential;
    }

    /**
     * 发送请求
     *
     * @param request 请求体
     * @return 接口响应结果
     */
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

        //TODO 优化if-else
        if (HttpMethod.GET.matches(method)) {
            String pathParams = SignUtils.generateGetRequestParams(interfaceParams);
            //GET 请求需要将参数拼接在路径后面
            String path = request.getUrl() + pathParams;
            String targetUrl = GATEWAY_HOST + path;
            logger.info("GET 请求目标地址:[{}]", targetUrl);
            String response = HttpUtil.get(targetUrl);
            ApiResponse apiResponse;
            try {
                apiResponse = JSON.parseObject(response, ApiResponse.class);
            } catch (Exception e) {
                //这里捕捉不单是解析异常的问题，还可能是返回错误页面的<html>代码
                apiResponse = ApiResponse.fail();
            }
            return apiResponse;
        } else if (HttpMethod.POST.matches(method)) {
            String targetUrl = GATEWAY_HOST + request.getUrl();
            logger.info("POST 请求目标地址:[{}]", targetUrl);
            Map<String, Object> requestParams = SignUtils.generatePostRequestParams(interfaceParams);
            String paramsJson = JSON.toJSONString(requestParams);
            String response = HttpRequest.post(targetUrl).body(paramsJson).execute().body();
            ApiResponse apiResponse;
            try {
                apiResponse = JSON.parseObject(response, ApiResponse.class);
            } catch (Exception e) {
                apiResponse = ApiResponse.fail();
            }
            return apiResponse;
        }
        return ApiResponse.fail(ErrorCode.OPERATION_ERROR);
    }
}
