package com.example.api.common.utils;

import cn.hutool.crypto.SecureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author fzy
 * @description: 签名工具类
 * @Date 2023/4/8 9:37
 */
public class SignUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignUtils.class);

    public static String createSign(Map<String, String> params) {
        //排序
        TreeMap<String, String> treeMap = new TreeMap<>(params);
        //排除sign参数
        treeMap.remove("sign");
        logger.info("排序后的参数:{}", treeMap);
        String paramsStr = mapToString(treeMap);
        logger.info("加密前字符串:{}", paramsStr);
        return SecureUtil.md5(paramsStr);
    }

    /**
     * 将map拼接成k1=v1&k2=v2的形式
     */
    public static String mapToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString().toUpperCase();
    }

}
