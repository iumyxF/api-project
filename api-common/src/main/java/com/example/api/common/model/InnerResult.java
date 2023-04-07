package com.example.api.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fzy
 * @description: 统一结果返回 （用于内部接口调用）
 * @Date 2023/4/7 9:54
 */
@Data
public class InnerResult<T> implements Serializable {

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

    public static <T> InnerResult<T> ok() {
        return restInnerResult(null, SUCCESS, "操作成功");
    }

    public static <T> InnerResult<T> ok(T data) {
        return restInnerResult(data, SUCCESS, "操作成功");
    }

    public static <T> InnerResult<T> ok(String msg) {
        return restInnerResult(null, SUCCESS, msg);
    }

    public static <T> InnerResult<T> ok(String msg, T data) {
        return restInnerResult(data, SUCCESS, msg);
    }

    public static <T> InnerResult<T> fail() {
        return restInnerResult(null, FAIL, "操作失败");
    }

    public static <T> InnerResult<T> fail(String msg) {
        return restInnerResult(null, FAIL, msg);
    }

    public static <T> InnerResult<T> fail(T data) {
        return restInnerResult(data, FAIL, "操作失败");
    }

    public static <T> InnerResult<T> fail(String msg, T data) {
        return restInnerResult(data, FAIL, msg);
    }

    public static <T> InnerResult<T> fail(int code, String msg) {
        return restInnerResult(null, code, msg);
    }

    private static <T> InnerResult<T> restInnerResult(T data, int code, String msg) {
        InnerResult<T> r = new InnerResult<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
}
