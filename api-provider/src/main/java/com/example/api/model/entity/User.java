package com.example.api.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 *
 * @author iumyxF
 */
@Data
public class User implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

}