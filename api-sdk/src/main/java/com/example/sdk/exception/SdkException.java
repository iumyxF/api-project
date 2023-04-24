package com.example.sdk.exception;


import com.example.sdk.model.ErrorCode;

/**
 * 自定义异常类
 *
 * @author iumyxF
 */
public class SdkException extends RuntimeException {

    private final int code;

    public SdkException(int code, String message) {
        super(message);
        this.code = code;
    }

    public SdkException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public SdkException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
