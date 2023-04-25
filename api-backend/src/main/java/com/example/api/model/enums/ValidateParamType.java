package com.example.api.model.enums;

import com.alibaba.fastjson2.JSONObject;

/**
 * @author fzy
 * @description: 参数类型校验 8 种 byte short int float double long [string\char] [array\list]
 * @date 2023/4/18 16:31
 */
public enum ValidateParamType {

    /**
     * byte
     */
    BYTE {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getByte(paramName);
        }
    },
    /**
     * short
     */
    SHORT {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getShort(paramName);
        }
    },
    /**
     * char
     */
    CHAR {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getString(paramName);
        }
    },
    /**
     * int
     */
    INT {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getInteger(paramName);
        }
    },
    /**
     * float
     */
    FLOAT {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getFloat(paramName);
        }
    },
    /**
     * double
     */
    DOUBLE {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getDouble(paramName);
        }
    },
    /**
     * long
     */
    LONG {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getLong(paramName);
        }
    },
    /**
     * array
     */
    ARRAY {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getJSONArray(paramName);
        }
    },
    /**
     * 字符类型
     */
    STRING {
        @Override
        public boolean isLegal(JSONObject userObject, String paramName) {
            return null != userObject.getString(paramName);
        }
    };

    /**
     * 从用户JSON中取出参数
     *
     * @param userObject 用户传入的参数
     * @param paramName  参数名
     */

    public abstract boolean isLegal(JSONObject userObject, String paramName);

}
