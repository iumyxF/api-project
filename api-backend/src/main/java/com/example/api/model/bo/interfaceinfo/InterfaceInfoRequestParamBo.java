package com.example.api.model.bo.interfaceinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fzy
 * @description: 接口调用参数对象
 * @date 2023/5/11 10:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceInfoRequestParamBo {

    /**
     * 接口参数名
     */
    private String name;

    /**
     * 接口参数类型
     */
    private String type;
}
