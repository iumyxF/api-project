package com.example.sdk.utils;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.sdk.exception.SdkException;
import com.example.sdk.model.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具
 *
 * @author feng
 * @date 2023/4/18 19:42
 */
public class SignUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignUtils.class);

    /**
     * 生成GET请求参数
     * http://localshot:8080/interface/invoke?k1=v1&k2=v2....
     * 目标字符串：?k1=v1&k2=v2....
     *
     * @param params 用户参数
     * @return 拼接好的参数[k1=v1&k2=v2]
     */
    public static String generateGetRequestParams(Map<String, Object> params) {
        Map<String, Object> processParams = generatePostRequestParams(params);
        StringBuilder builder = new StringBuilder(mapToString(processParams));
        StringBuilder result = builder.insert(0, "?");
        return result.toString();
    }

    /**
     * 生成POST请求参数
     *
     * @param params 用户参数
     * @return 封装好的接口请求参数
     */
    public static Map<String, Object> generatePostRequestParams(Map<String, Object> params) {
        //校验参数
        Long timestamp = (Long) params.get("timestamp");
        Long nonce = (Long) params.get("nonce");
        String accessKey = (String) params.get("accessKey");
        String secretKey = (String) params.get("secretKey");
        if (null == timestamp || timestamp <= 0) {
            throw new SdkException(ErrorCode.PARAMS_ERROR, "timestamp参数不合法");
        }
        if (null == nonce || nonce <= 0) {
            throw new SdkException(ErrorCode.PARAMS_ERROR, "nonce参数不合法");
        }
        if (StringUtils.isBlank(accessKey)) {
            throw new SdkException(ErrorCode.PARAMS_ERROR, "accessKey参数不合法");
        }
        if (StringUtils.isBlank(secretKey)) {
            throw new SdkException(ErrorCode.PARAMS_ERROR, "secretKey参数不合法");
        }
        String sign = createSign(params);
        params.put("sign", sign);
        return params;
    }

    /**
     * 加密流程
     * 1.全部参数按照参数名排序
     * 2.将参数拼接成k1=v1&k2=v2...形式
     * 3.md5加密上面的字符串并转成大写
     *
     * @param params 用户参数
     * @return
     */
    public static String createSign(Map<String, Object> params) {
        // 排序
        TreeMap<String, Object> treeMap = new TreeMap<>(params);
        logger.debug("排序后的参数:{}", treeMap);
        String paramsStr = mapToString(treeMap).toUpperCase();
        logger.debug("加密前字符串:{}", paramsStr);
        return SecureUtil.md5(paramsStr);
    }

    /**
     * 将map拼接成k1=v1&k2=v2的形式
     */
    public static String mapToString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
