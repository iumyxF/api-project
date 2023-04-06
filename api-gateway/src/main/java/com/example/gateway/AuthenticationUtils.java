package com.example.gateway;

import cn.hutool.crypto.SecureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import java.util.*;

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
     * 暂时性AccessKey
     */
    private final static String MY_ACCESS_KEY = "zoe";

    /**
     * 暂时性SecretKey
     */
    private final static String MY_SECRET_KEY = "ari";

    /**
     * 有效时间5分钟，单位毫秒
     */
    private final static Long EFFECTIVE_TIME = 5L * 60 * 1000;

    /**
     * 暂时性nonce
     */
    private final static String MY_NONCE = "123456";

    /**
     * GET 鉴权
     */
    public static boolean authenticationGetRequest(MultiValueMap<String, String> params) {
        logger.info("GET 鉴权");
        long currentTimeMillis = System.currentTimeMillis();
        //获取参数
        String accessKey = params.getFirst("accessKey");
        String timestamp = params.getFirst("timestamp");
        String nonce = params.getFirst("nonce");
        String sign = params.getFirst("sign");
        // 校验accessKey
        if (!MY_ACCESS_KEY.equals(accessKey)) {
            return false;
        }
        // 校验时间戳
        if (timestamp == null || currentTimeMillis - Long.parseLong(timestamp) > EFFECTIVE_TIME) {
            return false;
        }
        // 校验随机数nonce TODO redis
        if (!MY_NONCE.equals(nonce)) {
            return false;
        }
        //将参数转为treeMap
        TreeMap<String, String> treeMap = sortMultiValueMapToTreeMap(params);
        treeMap.remove("sign");
        // 检验sign
        return null != sign && checkSign(sign, treeMap);
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

    /**
     * 校验sign
     *
     * @param targetSign 获取的sign
     * @param treeMap    其他参数集合
     * @return 结果
     */
    private static boolean checkSign(String targetSign, TreeMap<String, String> treeMap) {
        String paramString = mapToString(treeMap);
        String sign = SecureUtil.md5(paramString);
        return targetSign.equals(sign);
    }

    /**
     * 将map拼接成k1=v1&k2=v2的形式
     */
    public static String mapToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        //todo secretKey从数据库中查询获取
        sb.append("&secretKey=" + MY_SECRET_KEY);
        return sb.toString().toUpperCase();
    }

    public static TreeMap<String, String> sortMultiValueMapToTreeMap(MultiValueMap<String, String> params) {
        TreeMap<String, String> sortedMap = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            sortedMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return sortedMap;
    }
}
