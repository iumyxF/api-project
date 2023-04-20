package com.example.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.annotation.AuthCheck;
import com.example.api.common.*;
import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.common.model.entity.User;
import com.example.api.constant.CommonConstant;
import com.example.api.constant.SystemConstant;
import com.example.api.exception.BusinessException;
import com.example.api.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.example.api.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.example.api.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.example.api.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.example.api.model.enums.InterfaceInfoStatusEnum;
import com.example.api.service.InterfaceInfoService;
import com.example.api.service.UserInterfaceInfoService;
import com.example.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 帖子接口
 *
 * @author iumyxF
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    // region 增删改查

    /**
     * 创建
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // content 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > SystemConstant.MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion

    /**
     * 接口上线
     *
     * @param updateStatusRequest 状态更新请求体
     */
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody UpdateStatusRequest updateStatusRequest) {
        //参数判断
        if (Objects.isNull(updateStatusRequest) || updateStatusRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = updateStatusRequest.getId();
        //接口是否存在
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (Objects.isNull(interfaceInfo)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // TODO 接口是否能调用
        /*
        要使用client进行判断 改造成SpringCloud用nacos进行调用，或者拆分项目，用dubbo调用
        starter 封装redis操作、日志、基本的东西
         */

        //更新接口状态
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 接口下线
     *
     * @param updateStatusRequest 状态更新请求体
     */
    @PostMapping("/offline")
    public BaseResponse<Boolean> offInterfaceInfo(@RequestBody UpdateStatusRequest updateStatusRequest) {
        //参数判断
        if (Objects.isNull(updateStatusRequest) || updateStatusRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = updateStatusRequest.getId();
        //接口是否存在
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (Objects.isNull(interfaceInfo)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //更新接口状态
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    @PostMapping("/invoke")
    public BaseResponse<Object> invoke(@RequestBody InterfaceInfoInvokeRequest invokeRequest, HttpServletRequest request) {
        //参数校验
        if (null == invokeRequest || invokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = invokeRequest.getId();
        String requestParams = invokeRequest.getUserRequestParams();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (null == interfaceInfo) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //对比校验接口参数和用户参数，并且将用户参数转成map格式
        Map<String, Object> userRequestParams = interfaceInfoService.validAndGetRequestParams(interfaceInfo.getRequestParams(), requestParams);

        //获取当前登录用户
        User user = userService.getLoginUser(request);
        if (null == user) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户信息不存在");
        }

        //校验是否有调用次数
        userInterfaceInfoService.verifyInvokeUserInterfaceInfo(user.getId(), interfaceInfo.getId());

        //TODO 调用
        if (HttpMethod.GET.matches(interfaceInfo.getMethod().toUpperCase())) {

        } else {

        }
        //调用返回的结果是 BaseResponse 将他转成JSONObject获取其中的data直接返回
        return null;
    }
}
