package com.example.api.common;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author fzy
 * @description:
 * @date 2023/7/12 16:55
 */
public class JsonTypeUtils {

    /**
     * 判断JSON对象中value类型是否符合type
     *
     * @param obj  JSONObject
     * @param type 类型
     * @param key  key
     * @return 结果
     */
    public static boolean isValueOfType(JSONObject obj, String type, String key) {
        Object value = obj.get(key);
        if (value == null) {
            return false;
        }
        switch (type.toUpperCase()) {
            case "INT":
            case "INTEGER":
                return value instanceof Integer;
            case "DOUBLE":
                return value instanceof Double;
            case "LONG":
                return value instanceof Long;
            case "STRING":
            case "CHAR":
                return value instanceof String;
            case "LIST":
                return value instanceof JSONArray;
            // 可以根据需要添加其他类型的判断条件
            default:
                return false;
        }
    }

}
