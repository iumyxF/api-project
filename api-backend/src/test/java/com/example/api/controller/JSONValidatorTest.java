package com.example.api.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * @author fzy
 * @description:
 * @date 2023/4/18 15:46
 */
public class JSONValidatorTest {

    // 一个用于校验参数的JSON(称它为模板JSON)
    private static final String templateJson = "[{\"name\":\"username\",\"type\":\"string\",\"required\":\"true\"},{\"name\":\"age\",\"type\":\"Integer\",\"required\":\"true\"}]";

    // 一个枚举类型，表示不同的数据类型
    private enum DataType {
        // 字符串类型
        STRING {
            @Override
            public void validate2(JSONObject userObject, String name) throws JSONException {
                // 调用JSONObject的getString方法获取用户JSON中该参数的值
                userObject.getString(name);
            }
        },
        // 整数类型
        INTEGER {
            @Override
            public void validate2(JSONObject userObject, String name) throws JSONException {
                // 调用JSONObject的getInt方法获取用户JSON中该参数的值
                userObject.getInt(name);
            }
        };

        // 一个抽象方法，根据不同的类型校验用户JSON中该参数的值
        public abstract void validate2(JSONObject userObject, String name) throws JSONException;
    }


    /**
     * 一个方法按照模板JSON来校验用户JSON的合法性
     * {"name":"jack","age":18}
     * [{"name":"username","type":"string","required":"true"},{"name":"age","type":"Integer","required":"true"}]
     */
    public static boolean validate(String userJson) {
        try {
            // 将模板JSON转换为JSONArray对象
            JSONArray templateArray = new JSONArray(templateJson);
            // 将用户JSON转换为JSONObject对象
            JSONObject userObject = new JSONObject(userJson);
            // 遍历模板数组中的每个元素
            for (int i = 0; i < templateArray.length(); i++) {
                // 获取当前元素，它是一个JSONObject对象
                JSONObject templateObject = templateArray.getJSONObject(i);
                // 获取当前元素的name属性，它代表参数名
                String name = templateObject.getString("name");
                // 获取当前元素的type属性，它代表数据类型
                String type = templateObject.getString("type").toLowerCase();
                // 获取当前元素的required属性，它代表是否必须
                boolean required = templateObject.getBoolean("required");
                // 如果一个参数required等于true，则判断用户JSON是否存在
                if (required) {
                    // 如果用户JSON不存在该参数，则返回false
                    if (!userObject.has(name)) {
                        return false;
                    }
                    // 如果用户JSON存在该参数，则判断它的值类型是否和模板中的类型一致
                    else {
                        // 根据不同的类型，调用不同的方法获取用户JSON中该参数的值
                        switch (type) {
                            case "string":
                                userObject.getString(name);
                                break;
                            case "integer":
                            case "int":
                                userObject.getInt(name);
                                break;
                            // 如果有其他类型，可以添加更多的case分支
                            default:
                                return false;
                        }
                    }
                }
            }
            // 如果没有发生任何异常，则返回true
            return true;
        } catch (JSONException e) {
            // 如果发生任何异常，则返回false
            return false;
        }
    }


    public static boolean validate2(String userJson) {
        try {
            // 将模板JSON转换为JSONArray对象
            JSONArray templateArray = new JSONArray(templateJson);
            // 将用户JSON转换为JSONObject对象
            JSONObject userObject = new JSONObject(userJson);
            // 遍历模板数组中的每个元素
            for (int i = 0; i < templateArray.length(); i++) {
                // 获取当前元素，它是一个JSONObject对象
                JSONObject templateObject = templateArray.getJSONObject(i);
                // 获取当前元素的name属性，它代表参数名
                String name = templateObject.getString("name");
                // 获取当前元素的type属性，它代表数据类型
                String type = templateObject.getString("type");
                // 获取当前元素的required属性，它代表是否必须
                boolean required = templateObject.getBoolean("required");
                // 如果一个参数required等于true，则判断用户JSON是否存在
                if (required) {
                    // 如果用户JSON不存在该参数，则返回false
                    if (!userObject.has(name)) {
                        return false;
                    }
                    // 如果用户JSON存在该参数，则判断它的值类型是否和模板中的类型一致
                    else {
                        // 根据不同的类型，从枚举中获取相应的枚举值，然后调用其validate方法校验用户JSON中该参数的值
                        DataType.valueOf(type.toUpperCase()).validate2(userObject, name);
                    }
                }
            }
            // 如果没有发生任何异常，则返回true
            return true;
        } catch (JSONException e) {
            // 如果发生任何异常，则返回false
            return false;
        }
    }

    public static void main(String[] args) {
        // 一个用户输入的JSON(称它为用户JSON)
        String userJson = "{\"username\":\"jack\",\"age\":18}";
        // 调用校验方法，打印结果
        System.out.println(validate2(userJson));
    }
}
