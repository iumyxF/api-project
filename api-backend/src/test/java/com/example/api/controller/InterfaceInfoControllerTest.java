package com.example.api.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", "jack");
        map.put("nonce", "123456");
        map.put("timestamp", String.valueOf(millis));
        map.put("accessKey", "zoe");
        map.put("secretKey", "ari");
        String sign = creatSign(map);
        map.put("sign", sign);
        map.remove("secretKey");
        System.out.println("sign值 = " + sign);
        StringBuilder path = mapToString(map);
        String url = "127.0.0.1:8082/provider/name/get?" + path;
        System.out.println("url = " + url);
        String s = HttpUtil.get(url);
        System.out.println(s);
    }

    @Test
    public void invokeInterfacePost() {
        String url = "127.0.0.1:8082/provider/name/post";
        HashMap<String, Object> map = new HashMap<>();
        //校验参数
        map.put("nonce", 123456);
        map.put("timestamp", System.currentTimeMillis());
        map.put("accessKey", "zoe");
        map.put("secretKey", "ari");
        //接口参数
        map.put("id", 1001);
        map.put("userName", "lisi");
        String sign = creatSign(map);
        map.put("sign", sign);
        System.out.println("sign值 = " + sign);

        map.remove("secretKey");

        String json = JSON.toJSONString(map);
        System.out.println("json = " + json);
        String res = HttpRequest.post(url)
                .body(json)
                .execute().body();
        System.out.println(res);
    }

    private String creatSign(HashMap<String, Object> map) {
        TreeMap<String, Object> treeMap = MapUtil.sort(map);
        System.out.println("排序后的参数 : " + treeMap);
        StringBuilder builder = mapToString(treeMap);
        String s = builder.toString().toUpperCase();
        System.out.println("加密前的字符串 : " + s);
        return SecureUtil.md5(s);
    }

    public static StringBuilder mapToString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb;
    }

    @Test
    public void pareJsonTest() {
        String json = "{\"id\":1001,\"userName\":\"lisi\"}";
        JSONObject obj = JSON.parseObject(json); // 将JSON字符串解析为JSONObject对象
        // 遍历JSONObject对象的所有属性
        // 将属性名和属性值放到Map中
        // 创建一个有序的Map
        Map<String, Object> map = new LinkedHashMap<>(obj);
        System.out.println(map); // 输出Map
    }

    @Test
    public void pingFun() throws IOException {
        int timeOut = 3000;  //超时应该在3钞以上
        boolean status = InetAddress.getByName("192.168.2.123").isReachable(timeOut);     // 当返回值是true时，说明host是可用的，false则不可。
        System.out.println(status);
    }
}
