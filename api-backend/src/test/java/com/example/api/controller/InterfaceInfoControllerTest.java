package com.example.api.controller;


import cn.hutool.http.HttpUtil;
import org.junit.Test;

import java.util.HashMap;

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
        String url = "127.0.0.1:8082/provider/name/get?name=jack";
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
}
