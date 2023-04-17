package com.example.api.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.common.ErrorCode;
import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.constant.SystemConstant;
import com.example.api.exception.BusinessException;
import com.example.api.mapper.InterfaceInfoMapper;
import com.example.api.model.dto.interfaceinfo.InterfaceParamDto;
import com.example.api.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author iumyxF
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @date 2023-03-04 22:29:12
 */
@Slf4j
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * interfaceInfo 参数校验
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String params = interfaceInfo.getRequestParams();
        String requestHeader = interfaceInfo.getRequestHeader();
        String method = interfaceInfo.getMethod();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, url, requestHeader, method, params)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > SystemConstant.MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public InterfaceInfo selectInterfaceInfoByUrlAndMethod(String url, String method) {
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<InterfaceInfo>()
                .eq(InterfaceInfo::getUrl, url)
                .eq(InterfaceInfo::getMethod, method);
        return interfaceInfoMapper.selectOne(wrapper);
    }

    /**
     * 用户请求参数: {"name":"jackeyLove","age":18}
     * 接口请求参数: [{"name":"username","type":"string","required":"true"},{"name":"age","type":"Integer","required":"true"}]
     *
     * @param userRequestParams          用户请求参数
     * @param interfaceInfoRequestParams 接口请求参数
     */
    @Override
    public void validRequestParams(String userRequestParams, String interfaceInfoRequestParams) {
        JSONObject userParams = JSONObject.parse(userRequestParams);
        List<InterfaceParamDto> paramDtoList = JSON.parseArray(interfaceInfoRequestParams, InterfaceParamDto.class);
        for (InterfaceParamDto param : paramDtoList) {
            String paramName = param.getName();
            String paramType = param.getType();
            String required = param.getRequired();
            //判断是否必须存在
            Object userParam = userParams.get(paramName);
            if (!Boolean.valueOf(required) && null == userParam) {
                continue;
            }
            //判断类型是否正确

        }
    }

    /**
     * 类型匹配转换
     *
     * @param typeName
     * @return 类
     */
    public Class typeConverter(String typeName) {
        String type = typeName.toLowerCase();
        if ("byte".equals(type)) {
            return Byte.class;
        } else if ("short".equals(type)) {
            return Short.class;
        } else if ("char".equals(type) || "character".equals(type)) {
            return Character.class;
        } else if ("int".equals(type) || "integer".equals(type)) {
            return Integer.class;
        } else if ("double".equals(type)) {
            return Double.class;
        } else if ("float".equals(type)) {
            return Float.class;
        } else if ("string".equals(type)) {
            return String.class;
        } else if ("array".equals(type) || "list".equals(type)) {
            return JSONArray.class;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        String json = "{\"name\":\"Tom\",\"age\":18,\"hobbies\":[\"basketball\",\"football\"]}";
        JSONObject jsonObject = JSONObject.parse(json);
        Object age = jsonObject.get("age");
        if (age instanceof Integer) {
            System.out.println("11");
        } else {
            System.out.println("222");
        }

        Object hobbies = jsonObject.get("hobbies");
        if (hobbies instanceof JSONArray) {
            System.out.println("1111");
        } else {
            System.out.println(222);
        }
        test("String");
    }

    public static <T> void test(String type) throws ClassNotFoundException {
        Class<T> clazz = (Class<T>) Class.forName(type);
        System.out.println(clazz.toString());
    }
}




