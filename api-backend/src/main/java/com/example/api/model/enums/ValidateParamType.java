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

    /**
     * 校验当前类型是否是字符类型
     *
     * @param typeName 类型名称
     * @return 结果
     */
    public static boolean isCharacterType(String typeName) {
        String type = typeName.toUpperCase();
        return STRING.name().equals(type) || CHAR.name().equals(type);
    }

    /**
     * 校验当前类型是否是数值类型
     *
     * @param typeName 类型名称
     * @return 结果
     */
    public static boolean isNumberType(String typeName) {
        String type = typeName.toUpperCase();
        return BYTE.name().equals(type) || SHORT.name().equals(type) || INT.name().equals(type) || FLOAT.name().equals(type) || DOUBLE.name().equals(type) || LONG.name().equals(type);
    }

}
