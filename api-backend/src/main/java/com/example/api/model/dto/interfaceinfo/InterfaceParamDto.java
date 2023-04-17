package com.example.api.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author feng 接口参数实体
 * @date 2023/4/17 20:08
 */
@Data
public class InterfaceParamDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数是否必填
     */
    private String required;

}
