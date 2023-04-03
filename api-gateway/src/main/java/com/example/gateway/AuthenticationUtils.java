package com.example.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 鉴权工具类
 * 1.获取请求参数
 * 2.判断AccessKey合法性：查询数据库是否分配过这对密钥
 * 3.重新加密签名，签名流程：
 * 3.1 按照请求参数名的字母升序排列非空请求参数（包含AccessKey），使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA
 * 3.2 在stringA最后拼接上SecretKey得到字符串stringSignTemp
 * 3.3 对stringSignTemp进行MD5运算，并将得到的字符串所有字符转换为大写，得到sign值。
 * 4. 对比两个签名是否相同
 * 5. 判断nonce是否重复使用
 * @Date 2023/4/3 14:10
 * @Author iumyxF
 */
public class AuthenticationUtils {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationUtils.class);

    /**
     * GET 鉴权
     */
    public static boolean authenticationGetRequest(MultiValueMap<String, String> params) {
        logger.info("GET 鉴权");
        //params.getFirst();
        return true;
    }

    /**
     * POST 鉴权
     */
    public static boolean authenticationPostRequest(String paramsString) {
        logger.info("POST 鉴权");
        //Map<String, String> params = parsePostParams(paramsString);
        return true;
    }

    /**
     * 解析post中的参数
     *
     * @param input name=rookie&k3=v3&k4=v4&k1=v1&k2=v2
     */
    private static Map<String, String> parsePostParams(String input) {
        Map<String, String> map = new HashMap<>(16);

        String[] pairs = input.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }
}
