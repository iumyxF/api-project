package com.example.api.service.impl;

import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.common.model.entity.User;
import com.example.api.common.model.entity.UserInterfaceInfo;
import com.example.api.exception.BusinessException;
import com.example.api.mapper.InterfaceInfoMapper;
import com.example.api.mapper.UserInterfaceInfoMapper;
import com.example.sdk.client.ApiClient;
import com.example.sdk.model.ApiRequest;
import com.example.sdk.model.ApiResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author fzy
 * @description:
 * @date 2023/7/12 9:12
 */
public class InterfaceInfoServiceImplTest {

    @Mock
    private InterfaceInfoMapper interfaceInfoMapper;

    @Mock
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Spy
    @InjectMocks
    private InterfaceInfoServiceImpl interfaceInfoServiceImpl;

    @Mock
    private InterfaceInfo interfaceInfo;

    @Mock
    private ApiClient apiClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 创建一个InterfaceInfo的实例，用于测试
    }

    @Test
    public void validInterfaceInfo_add_true() {
        interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(99999L);
        interfaceInfo.setName("test");
        interfaceInfo.setUrl("https://test.com");
        interfaceInfo.setRequestParams("name=test");
        interfaceInfo.setRequestHeader("Content-Type: application/json");
        interfaceInfo.setMethod("GET");
        interfaceInfo.setRequestParams("[{\"name\":\"username\",\"type\":\"string\",\"required\":\"true\"},{\"name\":\"age\",\"type\":\"int\",\"required\":\"true\"}]");
        // 调用mock对象的方法
        interfaceInfoServiceImpl.validInterfaceInfo(interfaceInfo, true);
        // 验证方法是否被调用一次，并且参数正确
        verify(interfaceInfoServiceImpl, times(1)).validInterfaceInfo(interfaceInfo, true);
        // 没有异常抛出，表示验证通过
    }

    @Test
    public void validInterfaceInfo_add_false() {
        // 修改interfaceInfo的一个属性，使其为空
        interfaceInfo.setName(null);
        // 调用mock对象的方法
        interfaceInfoServiceImpl.validInterfaceInfo(interfaceInfo, false);
        // 验证方法是否被调用一次，并且参数正确
        verify(interfaceInfoServiceImpl, times(1)).validInterfaceInfo(interfaceInfo, false);
        // 没有异常抛出，表示验证通过
    }

    @Test
    public void validInterfaceInfo_interfaceInfo_null() {
        // 传入一个null作为interfaceInfo参数
        Assertions.assertThrows(BusinessException.class, () -> {
            // 调用mock对象的方法
            interfaceInfoServiceImpl.validInterfaceInfo(null, true);
            // 验证方法是否被调用一次，并且参数正确
            verify(interfaceInfoServiceImpl, times(1)).validInterfaceInfo(null, true);
            // 应该抛出BusinessException异常，表示验证通过
        });
    }

    @Test
    public void validInterfaceInfo_name_blank() {
        // 修改interfaceInfo的name属性，使其为空字符串
        interfaceInfo.setName("");
        Assertions.assertThrows(BusinessException.class, () -> {
            // 调用mock对象的方法
            interfaceInfoServiceImpl.validInterfaceInfo(interfaceInfo, true);
            // 验证方法是否被调用一次，并且参数正确
            verify(interfaceInfoServiceImpl, times(1)).validInterfaceInfo(interfaceInfo, true);
            // 应该抛出BusinessException异常，表示验证通过
        });
    }

    @Test
    public void validInterfaceInfo_name_too_long() {
        // 修改interfaceInfo的name属性，使其超过最大长度限制
        interfaceInfo = new InterfaceInfo();
        interfaceInfo.setName("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttest");
        Assertions.assertThrows(BusinessException.class, () -> {
            // 调用mock对象的方法
            interfaceInfoServiceImpl.validInterfaceInfo(interfaceInfo, false);
            // 验证方法是否被调用一次，并且参数正确
            verify(interfaceInfoServiceImpl, times(1)).validInterfaceInfo(interfaceInfo, false);
            // 应该抛出BusinessException异常，表示验证通过
        });
    }

    @Test
    public void GivenUrlIsNull_WhenSelectInterfaceInfoByUrlAndMethod_ThenThrowBusinessException() {
        when(interfaceInfoMapper.selectByUrlAndMethod(anyString(), anyString())).thenReturn(interfaceInfo);
        Assertions.assertThrows(BusinessException.class, () -> {
            interfaceInfoServiceImpl.selectInterfaceInfoByUrlAndMethod(null, "method");
            verify(interfaceInfoServiceImpl, times(1)).selectInterfaceInfoByUrlAndMethod(null, "method");
        });
    }

    @Test
    public void GivenParamsNotNull_WhenSelectInterfaceInfoByUrlAndMethod_ThenReturnInterfaceInfo() {
        // 当mock对象的selectByUrlAndMethod方法被调用时，返回interfaceInfo
        when(interfaceInfoMapper.selectByUrlAndMethod("https://test.com", "GET")).thenReturn(interfaceInfo);
        // 调用interfaceInfoService的方法，不会真正执行数据库操作
        InterfaceInfo result = interfaceInfoServiceImpl.selectInterfaceInfoByUrlAndMethod("https://test.com", "GET");
        // 验证方法是否被调用一次，并且参数正确
        verify(interfaceInfoMapper, times(1)).selectByUrlAndMethod("https://test.com", "GET");
        // 断言返回的结果和预期的结果相同
        Assertions.assertEquals(interfaceInfo, result);
    }

    /**
     * 用户请求参数的标准格式：{"username":"emiria","age":18}
     * 接口请求参数的标准格式：[{"name":"username","type":"string","required":"true"},{"name":"age","type":"int","required":"true"}]
     */
    @Test
    public void WhenValidAndGetRequestParams_ThenGetParamsMap() {
        String userRequestParams = "{\"username\":\"emiria\",\"age\":18}";
        String interfaceInfoRequestParams = "[{\"name\":\"username\",\"type\":\"string\",\"required\":\"true\"},{\"name\":\"age\",\"type\":\"int\",\"required\":\"true\"}]";
        HashMap<String, Object> expectMap = new HashMap<>();
        expectMap.put("username", "emiria");
        expectMap.put("age", "18");

        // 此处会报错：使用when (spy.foo()).thenReturn ()语法来模拟一个spy对象的方法。
        // 这种方式可能会导致模拟失败，因为spy对象会真正地调用foo方法，而不是返回模拟的值。
        // 为了避免这种情况，您应该使用doReturn|Throw()系列的方法来模拟spy对象的方法
        when(interfaceInfoServiceImpl.validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams)).thenReturn(expectMap);

        //doReturn(expectMap).when(interfaceInfoServiceImpl).validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);

        Map<String, Object> result = interfaceInfoServiceImpl.validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);

        verify(interfaceInfoServiceImpl, times(1)).validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);

        Assertions.assertEquals(expectMap, result);
    }

    @Test
    public void GivenStringThatAreNotJSON_WhenValidAndGetRequestParams_ThenThrowBusinessException() {
        String userRequestParams = "test";
        String interfaceInfoRequestParams = "[{\"name\":\"username\",\"type\":\"string\",\"required\":\"true\"},{\"name\":\"age\",\"type\":\"int\",\"required\":\"true\"}]";
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            interfaceInfoServiceImpl.validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);
            verify(interfaceInfoServiceImpl, times(1)).validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);
        });
        Assertions.assertEquals("解析的参数不符合JSON格式", exception.getMessage());
    }

    @Test
    public void GivenMissingRequiredAttributes_WhenValidAndGetRequestParams_ThenThrowBusinessException() {
        String userRequestParams = "{\"age\":18}";
        String interfaceInfoRequestParams = "[{\"name\":\"username\",\"type\":\"string\",\"required\":\"true\"},{\"name\":\"age\",\"type\":\"int\",\"required\":\"false\"}]";
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            interfaceInfoServiceImpl.validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);
            verify(interfaceInfoServiceImpl, times(1)).validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);
        });
        Assertions.assertEquals("必填参数缺失", exception.getMessage());
    }

    @Test
    public void GivenTypeNotLegal_WhenValidAndGetRequestParams_ThenThrowBusinessException() {
        String userRequestParams = "{\"username\":\"emiria\",\"age\":\"test\"}";
        String interfaceInfoRequestParams = "[{\"name\":\"username\",\"type\":\"string\",\"required\":\"true\"},{\"name\":\"age\",\"type\":\"int\",\"required\":\"false\"}]";
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            interfaceInfoServiceImpl.validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);
            verify(interfaceInfoServiceImpl, times(1)).validAndGetRequestParams(userRequestParams, interfaceInfoRequestParams);
        });
        Assertions.assertEquals("参数类型不正确", exception.getMessage());
    }

    /**
     * 这个不会写了
     */
    @Ignore
    @Test
    public void testVerifyInterfaceIsAvailable() {
        // 创建测试数据
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(1L);
        interfaceInfo.setMethod("GET");
        interfaceInfo.setUrl("http://example.com/api");
        interfaceInfo.setRequestParams("[]");

        User loginUser = new User();
        loginUser.setId(1L);
        loginUser.setAccessKey("accessKey");
        loginUser.setSecretKey("secretKey");

        // 模拟依赖方法的行为
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        Mockito.when(userInterfaceInfoMapper.insert(Mockito.any(UserInterfaceInfo.class)))
                .thenReturn(1);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(HttpStatus.OK.value());
        apiResponse.setMsg("Response Data");
        Mockito.when(apiClient.sendRequest(Mockito.any(ApiRequest.class)))
                .thenReturn(apiResponse);

        // 调用被测试的方法
        interfaceInfoServiceImpl.verifyInterfaceIsAvailable(interfaceInfo, loginUser);

        // 验证结果
        Mockito.verify(userInterfaceInfoMapper, Mockito.times(1))
                .insert(Mockito.any(UserInterfaceInfo.class));

        Mockito.verify(apiClient, Mockito.times(1))
                .sendRequest(Mockito.any(ApiRequest.class));
    }

}