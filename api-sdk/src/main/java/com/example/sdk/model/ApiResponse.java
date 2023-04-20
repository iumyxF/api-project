package com.example.sdk.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fzy
 * @description: sdk统一结果返回
 * @Date 2023/4/7 9:54
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    private T data;

    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 失败
     */
    public static final int FAIL = 500;

    public static <T> ApiResponse<T> ok() {
        return restApiResponse(null, SUCCESS, "操作成功");
    }

    public static <T> ApiResponse<T> ok(T data) {
        return restApiResponse(data, SUCCESS, "操作成功");
    }

    public static <T> ApiResponse<T> ok(String msg) {
        return restApiResponse(null, SUCCESS, msg);
    }

    public static <T> ApiResponse<T> ok(String msg, T data) {
        return restApiResponse(data, SUCCESS, msg);
    }

    public static <T> ApiResponse<T> fail() {
        return restApiResponse(null, FAIL, "操作失败");
    }

    public static <T> ApiResponse<T> fail(String msg) {
        return restApiResponse(null, FAIL, msg);
    }

    public static <T> ApiResponse<T> fail(T data) {
        return restApiResponse(data, FAIL, "操作失败");
    }

    public static <T> ApiResponse<T> fail(String msg, T data) {
        return restApiResponse(data, FAIL, msg);
    }

    public static <T> ApiResponse<T> fail(int code, String msg) {
        return restApiResponse(null, code, msg);
    }

    private static <T> ApiResponse<T> restApiResponse(T data, int code, String msg) {
        ApiResponse<T> r = new ApiResponse<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
}
