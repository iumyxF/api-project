package com.example.api.common.service;

import com.example.api.common.model.InnerResult;
import com.example.api.common.model.entity.User;

/**
 * The interface Inner user service.
 *
 * @author fzy
 * @description:
 * @Date 2023 /4/8 9:17
 */
public interface InnerUserService {

    /**
     * Select user by access key inner result.
     *
     * @param accessKey the access key
     * @return the inner result
     */
    InnerResult<User> selectUserByAccessKey(String accessKey);

}
