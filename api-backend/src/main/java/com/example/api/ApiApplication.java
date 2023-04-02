package com.example.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author iumyxF
 * @date 2023/3/30 19:54
 */
@SpringBootApplication
@MapperScan("com.example.api.mapper")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
