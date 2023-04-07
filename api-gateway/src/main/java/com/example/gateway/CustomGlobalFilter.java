package com.example.gateway;

import com.example.api.common.model.InnerResult;
import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.common.service.DemoService;
import com.example.api.common.service.InnerInterfaceInfoService;
import com.example.api.common.service.InnerUserInterfaceInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author iumyxF
 * @date 2023/3/30 21:44
 */

@Slf4j
//@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final Set<String> WHITE_LIST = Collections.singleton("127.0.0.1");

    private final String FILE_UPLOAD_CONTENT_TYPE = "multipart/form-data";

    @Resource
    private ObjectMapper objectMapper;

    @DubboReference
    private DemoService demoService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    /**
     * 全局过滤器中需要进行的操作
     * 1. 请求日志处理
     * 2. 黑白名单
     * 3. 参数校验
     * 3.1 ak、sk合法性
     * 3.2 nonce 是否重复（15分钟内是否重复访问）
     * 3.3 timestamp 是否在合法的访问时间内（暂·定5分钟内有效）
     * 3.4 签名校验（将参数进行加密对比）
     * 4. 模拟接口是否存在(并不是直接调用目标接口) 不需要在这里判断了
     * 5. 请求转发，调用模拟接口
     * 6. 处理响应日志
     * 7. 根据响应日志（成功/失败），修改接口调用次数（加1/不变）
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return result
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求
        ServerHttpRequest request = exchange.getRequest();
        //获取响应
        ServerHttpResponse originalResponse = exchange.getResponse();
        URI uri = request.getURI();
        String path = request.getPath().value();
        String method = request.getMethodValue();
        HttpHeaders header = request.getHeaders();
        String host = Optional.ofNullable(request.getLocalAddress())
                .map(InetSocketAddress::getHostString)
                .orElse("");
        String requestParams = String.valueOf(request.getQueryParams());
        AtomicReference<String> requestBody = new AtomicReference<>("");
        //1. 请求日志处理
        log.info("***********************************请求信息**********************************");
        log.info("URI = {}", uri);
        log.info("method = {}", method);
        log.info("path = {}", path);
        log.info("header = {}", header);
        log.info("requestParams = {}", requestParams);

        //2. 黑白名单
        if (!WHITE_LIST.contains(host)) {
            return handlerNoAuth(originalResponse);
        }

        // 如果缺少请求类型或者是文件上传类型的请求，直接放行
        String contentType = header.getFirst("content-type");
        if (StringUtils.hasLength(contentType) && contentType.contains(FILE_UPLOAD_CONTENT_TYPE)) {
            return chain.filter(exchange);
        }

        // response 调用目标接口后获取的返回内容
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                body = fluxBody.buffer().map(dataBuffers -> {
                    DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                    DataBuffer join = dataBufferFactory.join(dataBuffers);
                    byte[] content = new byte[join.readableByteCount()];
                    join.read(content);
                    String responseData = new String(content, StandardCharsets.UTF_8);
                    //6. 处理响应日志
                    log.info("***********************************响应信息**********************************");
                    try {
                        // 7. 根据响应日志（成功/失败）
                        InnerResult innerResult = objectMapper.readValue(responseData, InnerResult.class);
                        if (HttpStatus.OK.value() == innerResult.getCode()) {
                            log.info("接口调用成功,响应信息:{}", innerResult);
                            //TODO 接口调用次数（加1/不变） 怎么获取接口id和用户id？
                            //innerUserInterfaceInfoService.incrementInterfaceCallCount();
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("****************************************************************************\n");
                    DataBufferUtils.release(join);

                    //TODO 优化 使用@Ansync异步方法日志入库
                    //修改返回内容，返回内容是JSON字符串，因此需要把JSON转成具体的对象再处理。
                    //R r = om.readValue(responseData, R.class);//R是统一泛型返回对象，这里因人而已，不具体介绍。
                    //String newContent = om.writeValueAsString(r);
                    //return bufferFactory.wrap(newContent.getBytes());
                    return bufferFactory.wrap(content);
                });
                return super.writeWith(body);
            }
        };
        log.info("****************************************************************************\n");

        /*
        POST 请求处理
        获取body，虽然该方法在后面，但是实际效果是在response前面
        Content-Length 是一个实体消息首部，用来指明发送给接收方的消息主体的大小1。
        对于 GET 请求，由于没有请求体，所以 Content-Length 通常为 -1。
        而对于 POST 请求，由于有请求体，所以 Content-Length 会大于0。
        */
        if (header.getContentLength() > 0) {
            return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                String bodyString = new String(bytes, StandardCharsets.UTF_8);
                //设置requestBody到变量，让response获取
                requestBody.set(bodyString);
                log.info("requestBody = {}", bodyString);
                //3.POST 校验 参数合法性
                boolean authed = AuthenticationUtils.authenticationPostRequest(bodyString);
                if (!authed) {
                    return handlerNoAuth(originalResponse);
                }
                //4.POST 接口是否存在
                InnerResult<InterfaceInfo> result = innerInterfaceInfoService.selectInterfaceInfo(path, method);
                if (null == result || HttpStatus.OK.value() != result.getCode()) {
                    return handlerInvokeError(originalResponse);
                }

                exchange.getAttributes().put("POST_BODY", bodyString);
                DataBufferUtils.release(dataBuffer);
                Flux<DataBuffer> cachedFlux = Flux.defer(() -> Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));

                ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return cachedFlux;
                    }
                };
                // 5.POST 请求转发
                return chain.filter(exchange.mutate().request(mutatedRequest).response(decoratedResponse).build());
            });
        } else {
            //GET 校验 参数合法性
            boolean authed = AuthenticationUtils.authenticationGetRequest(request.getQueryParams());
            if (!authed) {
                return handlerNoAuth(originalResponse);
            }
            // 4.GET 接口是否存在
            InnerResult<InterfaceInfo> result = innerInterfaceInfoService.selectInterfaceInfo(path, method);
            if (null == result || HttpStatus.OK.value() != result.getCode()) {
                return handlerInvokeError(originalResponse);
            }
        }

        // 5.GET 请求转发
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 无权限访问处理
     *
     * @param response 响应体
     * @return 无权限访问响应结果
     */
    private Mono<Void> handlerNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 调用失败处理
     *
     * @param response 响应体
     * @return 调用失败响应结果
     */
    private Mono<Void> handlerInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

}
