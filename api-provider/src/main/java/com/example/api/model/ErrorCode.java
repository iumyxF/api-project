package com.example.api.model;

/**
 * 错误码
 *
 * @author iumyxF
 */
public enum ErrorCode {

    /**
     * Success code.
     */
    SUCCESS(0, "ok"),
    /**
     * Params error code.
     */
    PARAMS_ERROR(40000, "请求参数错误"),
    /**
     * Not login error code.
     */
    NOT_LOGIN_ERROR(40100, "未登录"),
    /**
     * No auth error error code.
     */
    NO_AUTH_ERROR(40101, "无权限"),
    /**
     * Not found error code.
     */
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    /**
     * Forbidden error code.
     */
    FORBIDDEN_ERROR(40300, "禁止访问"),
    /**
     * System error code.
     */
    SYSTEM_ERROR(50000, "系统内部异常"),
    /**
     * Operation error code.
     */
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
