package com.example.gateway;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author iumyxF
 * @date 2023/3/30 21:44
 */

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final Set<String> white_list = Collections.singleton("127.0.0.1");

    /**
     * 全局路由拦截器，具体的业务逻辑处理
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.用户发送请求到AIP网关
        //2.请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识:" + request.getId());
        log.info("请求路径:" + request.getPath().value());
        log.info("请求方法:" + request.getMethod().toString());
        log.info("请求参数:" + request.getQueryParams());
        String host = request.getLocalAddress().getHostString();
        log.info("请求来源:" + host);
        //3.黑白名单
        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        if (!white_list.contains(host)) {
            handlerNoAuth(response);
        }
        //4.鉴权（判断ak,sk是否合法）
        //获取请求头中的参数
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        //todo 判断该用户是否被分配accessKey了，要去数据库中查询
        if (!accessKey.contains("test")) {
            //
            return handlerNoAuth(response);
        }
        //校验下随机数是否合法
        if (Long.parseLong(nonce) > 10000) {
            //
            return handlerNoAuth(response);
        }
        //时间判断不超过5分钟
        long currentTime = System.currentTimeMillis() / 1000;
        Long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handlerNoAuth(response);
        }
        //todo 校验密钥 通过provider-starter校验


        //5.请求的模拟接口是否存在
        //todo 从数据库中查询interface_info中是否存在该接口（远程调用api-backend，http or rpc）

        //6.请求转发，调用模拟接口

        // 解决方案 应该使用内置的装饰者模式 ,https://blog.csdn.net/qq_19636353/article/details/126759522
        //7.处理响应
        //return handlerResponse(exchange, chain);

        //其他
        return chain.filter(exchange).then(Mono.fromRunnable(()->{

        }));
    }

    /**
     * 难点 需要理解一下
     */
    public Mono<Void> handlerResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            //获取原response
            ServerHttpResponse originalResponse = exchange.getResponse();
            //获取缓冲区工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //获取响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                //装饰response
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    /**
                     * 调用接口完毕后执行的方法
                     */
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));

                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //向response中写入数据，构造数据拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                sb2.append("<--- {} {} \n");
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                // data 就是接口返回值

                                //todo 8.调用成功，接口调用次数+1 远程调用


                                //9.调用失败，返回一个错误码
                                //log.info("custom global filter");
                                //if (response.getStatusCode() != HttpStatus.OK) {
                                //    return handlerInvokeError(response);
                                //}

                                log.info(sb2.toString(), rspArgs.toArray());//log.info("<-- {} {}\n", originalResponse.getStatusCode(), data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };

                // 设置response对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> handlerNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handlerInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

}
