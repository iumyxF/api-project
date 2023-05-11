package com.example.api.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.api.model.bo.interfaceinfo.InterfaceInfoRequestParamBo;
import com.example.api.model.enums.ValidateParamType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fzy
 * @description: 接口工具类
 * @date 2023/5/11 11:03
 */
public class InterfaceInfoUtils {

    /**
     * 根据参数模板模拟接口参数数据
     *
     * @param paramsTemplate 接口请求参数模板
     */
    public static Map<String, Object> mockInterfaceRequestParam(List<InterfaceInfoRequestParamBo> paramsTemplate) {
        if (CollUtil.isEmpty(paramsTemplate)) {
            return new HashMap<>(1);
        }
        HashMap<String, Object> res = new HashMap<>(16);
        for (InterfaceInfoRequestParamBo requestParamBo : paramsTemplate) {
            String name = requestParamBo.getName();
            String type = requestParamBo.getType();
            Object value;
            //TODO 校验参数类型可以完善
            if (ValidateParamType.isNumberType(type)) {
                value = RandomUtil.randomNumbers(5);
            } else {
                value = RandomUtil.randomString(5);
            }
            res.put(name, value);
        }
        return res;
    }


}
