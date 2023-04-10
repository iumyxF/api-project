package com.example.api.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author feng
 * @date 2023/4/10 19:47
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口主键
     */
    private Long id;

    /**
     * 用户请求参数JSON
     */
    private String userRequestParams;

}
