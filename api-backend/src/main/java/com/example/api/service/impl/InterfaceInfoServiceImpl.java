package com.example.api.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.common.ErrorCode;
import com.example.api.common.InterfaceInfoUtils;
import com.example.api.common.JsonTypeUtils;
import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.common.model.entity.User;
import com.example.api.common.model.entity.UserInterfaceInfo;
import com.example.api.constant.SystemConstant;
import com.example.api.exception.BusinessException;
import com.example.api.mapper.InterfaceInfoMapper;
import com.example.api.mapper.UserInterfaceInfoMapper;
import com.example.api.model.bo.interfaceinfo.InterfaceInfoRequestParamBo;
import com.example.api.service.InterfaceInfoService;
import com.example.sdk.client.ApiClient;
import com.example.sdk.model.ApiRequest;
import com.example.sdk.model.ApiResponse;
import com.example.sdk.model.Credential;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

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
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return interfaceInfoMapper.selectByUrlAndMethod(url, method);
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
        //检验参数是否为JSON格式
        if (!JSON.isValid(userRequestParams) || !JSON.isValid(interfaceInfoRequestParams)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "解析的参数不符合JSON格式");
        }
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
            boolean isLegal = JsonTypeUtils.isValueOfType(userObject, type, name);
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

    /**
     * 校验接口能否使用
     *
     * @param interfaceInfo 接口对象
     * @param loginUser     当前登录用户
     */
    @Override
    public void verifyInterfaceIsAvailable(InterfaceInfo interfaceInfo, User loginUser) {
        //校验 调用 看能否正常返回数据

        //TODO 这里是否要改造成管理员能访问所有接口呢？
        //新增当前管理员对该接口调用次数
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setUserId(loginUser.getId());
        userInterfaceInfo.setInterfaceInfoId(interfaceInfo.getId());
        userInterfaceInfo.setTotalNum(1);
        userInterfaceInfo.setLeftNum(1);
        userInterfaceInfo.setStatus(0);
        userInterfaceInfoMapper.insert(userInterfaceInfo);

        //创建模拟接口的数据
        Map<String, Object> mockParams = new HashMap<>(16);
        String requestParamsJson = interfaceInfo.getRequestParams();
        if (StringUtils.isNotBlank(requestParamsJson)) {
            List<InterfaceInfoRequestParamBo> requestParamBoList = JSON.parseArray(requestParamsJson, InterfaceInfoRequestParamBo.class);
            mockParams = InterfaceInfoUtils.mockInterfaceRequestParam(requestParamBoList);
        }
        //调用接口
        Credential credential = new Credential(loginUser.getAccessKey(), loginUser.getSecretKey());
        ApiClient client = new ApiClient(credential);
        //解析请求方式
        HttpMethod resolve = HttpMethod.resolve(interfaceInfo.getMethod().toUpperCase());
        if (null == resolve) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口请求方式异常,上线失败");
        }
        String requestMethod = resolve.name();
        //创建请求对象
        ApiRequest apiRequest = new ApiRequest(requestMethod, interfaceInfo.getUrl(), mockParams);
        //发送请求
        ApiResponse apiResponse = client.sendRequest(apiRequest);
        if (null == apiResponse || HttpStatus.OK.value() != apiResponse.getCode()) {
            throw new BusinessException(ErrorCode.REMOTE_CALL_ERROR, "接口连接异常,上线失败");
        }
    }
}




