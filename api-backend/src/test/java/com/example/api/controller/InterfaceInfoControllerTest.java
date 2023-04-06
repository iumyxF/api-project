package com.example.api.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description:
 * @Date 2023/4/3 11:21
 * @Author iumyxF
 */
public class InterfaceInfoControllerTest {

    /**
     * 调用远程接口
     */
    @Test
    public void invokeInterfaceGet() {
        long millis = System.currentTimeMillis();
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "jack");
        map.put("nonce", "123456");
        map.put("timestamp", String.valueOf(millis));
        map.put("accessKey", "zoe");
        String sign = creatSign(map);
        map.put("sign", sign);
        System.out.println("sign值 = " + sign);
        StringBuilder path = mapToString(map);
        String url = "127.0.0.1:8082/provider/name/get?" + path;
        System.out.println(path);
        String s = HttpUtil.get(url);
        System.out.println(s);
    }

    @Test
    public void invokeInterfacePost() {
        String url = "127.0.0.1:8082/provider/name/post";
        HashMap<String, Object> params = new HashMap<>(5);
        params.put("name", "rookie");
        params.put("k1", "v1");
        params.put("k2", "v2");
        params.put("k3", "v3");
        params.put("k4", "v4");
        String s = HttpUtil.post(url, params);
        System.out.println(s);
    }

    private String creatSign(HashMap<String, String> map) {
        TreeMap<String, String> treeMap = MapUtil.sort(map);
        System.out.println("排序后的参数 : " + treeMap);
        StringBuilder builder = mapToString(treeMap);
        builder.append("&secretKey=ari");
        String s = builder.toString().toUpperCase();
        System.out.println("加密前的字符串 : " + s);
        return SecureUtil.md5(s);
    }

    public static StringBuilder mapToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb;
    }
}
