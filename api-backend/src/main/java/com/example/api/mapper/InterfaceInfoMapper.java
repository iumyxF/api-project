package com.example.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.api.common.model.entity.InterfaceInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @author iumyxF
 * @description 针对表【interface_info(接口信息)】的数据库操作Mapper
 * @date 2023-03-04 22:29:12
 */
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {

    /**
     * 通过url地址和请求方式查询接口
     *
     * @param url    接口地址
     * @param method 接口请求方式
     * @return 接口对象
     */
    InterfaceInfo selectByUrlAndMethod(@Param("url") String url, @Param("method") String method);

}




