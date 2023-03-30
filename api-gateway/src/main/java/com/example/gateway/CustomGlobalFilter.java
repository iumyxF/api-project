package com.example.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author feng
 * @date 2023/3/30 21:44
 */

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    /**
     * 全局路由拦截器，具体的业务逻辑处理
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.用户发送请求到AIP网关
        //2.请求日志
        //3.黑白名单
        //4.用户鉴权（判断ak,sk是否合法）
        //5.请求的模拟接口是否存在
        //6.请求转发，调用模拟接口
        //7.相应日志
        //8.调用成功，接口调用次数+1
        //9.调用失败，返回一个错误码
        log.info("custom global filter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

}