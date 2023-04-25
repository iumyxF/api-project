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
import com.example.api.model.enums.ValidateParamType;
import com.example.api.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
     * 用户请求参数: {"name":"emiria","age":18}
     * 接口请求参数: [{"name":"username","type":"string","required":"true"},{"name":"age","type":"Integer","required":"true"}]
     * 优化过程：switch-case变成枚举类
     *
     * @param userRequestParams          用户请求参数
     * @param interfaceInfoRequestParams 接口请求参数
     */
    @Override
    public Map<String, Object> validAndGetRequestParams(String userRequestParams, String interfaceInfoRequestParams) {
        HashMap<String, Object> resultParams = new HashMap<>(16);
        //模板JSON
        JSONArray templateArray = JSON.parseArray(interfaceInfoRequestParams);
        JSONObject userObject = JSONObject.parseObject(userRequestParams);
        //遍历校验参数
        for (int i = 0; i < templateArray.size(); i++) {
            JSONObject templateObject = templateArray.getJSONObject(i);
            //参数名
            String name = templateObject.getString("name");
            //参数类型
            String type = templateObject.getString("type");
            //参数是否必须
            boolean required = templateObject.getBooleanValue("required", false);

            //必填参数是否存在
            if (required && !userObject.containsKey(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "必填参数缺失");
            }
            //参数类型是否合法
            boolean isLegal = ValidateParamType.valueOf(type.toUpperCase()).isLegal(userObject, name);
            if (!isLegal) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数类型不正确");
            }
            //保存参数
            Object paramValue = userObject.get(name);
            if (null != paramValue) {
                resultParams.put(name, paramValue);
            }
        }
        return resultParams;
    }
}




