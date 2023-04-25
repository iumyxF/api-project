package com.example.api.controller;

import com.example.api.model.BaseResponse;
import com.example.api.model.ResultUtils;
import com.example.api.model.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author iumyxF 假设这里提供一个随机名字的接口
 * @date 2023/3/30 20:24
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public BaseResponse<String> getName(String username, HttpServletRequest request) {
        String result = "GET name = " + username;
        return ResultUtils.success(result);
    }

    @PostMapping("/post")
    public BaseResponse<String> postName(@RequestBody User user) {
        return ResultUtils.success("POST name = " + user.getUserName() + " and you id = " + user.getId());
    }

    @PostMapping("/enitiy")
    public String entityName(@RequestBody User user, HttpServletRequest request) {
        //...
        return "entity name = " + user.getUserName();
    }

}
