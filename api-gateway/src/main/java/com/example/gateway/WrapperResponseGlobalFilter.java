package com.example.gateway;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson2.JSONObject;
import com.example.api.common.model.InnerResult;
import com.example.api.common.model.entity.InterfaceInfo;
import com.example.api.common.model.entity.User;
import com.example.api.common.service.InnerInterfaceInfoService;
import com.example.api.common.service.InnerUserInterfaceInfoService;
import com.example.api.common.service.InnerUserService;
import com.example.gateway.constants.CacheConstants;
import com.example.gateway.utils.RedisCache;
import com.example.sdk.utils.SignUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author fzy
 * @description:
 * @Date 2023/4/7 15:33
 */
@Component
@Slf4j
public class WrapperResponseGlobalFilter implements GlobalFilter, Ordered {

    private static final Set<String> WHITE_LIST = Collections.singleton("127.0.0.1");

    /**
     * nonce默认最大缓存时间（毫秒）
     */
    @Value("${api.invoke.max-cache-time}")
    private Long nonceMaxCacheTime;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedisCache redisCache;

    @DubboReference
    private InnerUserService innerUserService;

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
     * 4. 请求转发，调用模拟接口
     * 5. 处理响应日志，接口次数回调
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return result
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();
        //1. 请求日志处理
        URI uri = serverHttpRequest.getURI();
        String path = serverHttpRequest.getPath().value();
        HttpMethod method = serverHttpRequest.getMethod();
        HttpHeaders header = serverHttpRequest.getHeaders();
        String host = Optional.ofNullable(serverHttpRequest.getLocalAddress())
                .map(InetSocketAddress::getHostString)
                .orElse("");
        log.info("***********************************请求信息**********************************");
        log.info("URI = {}", uri);
        log.info("path = {}", path);
        log.info("header = {}", header);
        log.info("*** method = {}", method);

        //2. 黑白名单
        if (!WHITE_LIST.contains(host)) {
            return handlerNoAuth(originalResponse);
        }
        //请求参数，post从请求里获取请求体
        Map<String, Object> params = wrapRequestParams(serverHttpRequest, originalResponse, method);
        if (MapUtil.isEmpty(params)) {
            return handlerNoAuth(originalResponse);
        }
        log.info("*** params = {}", params);

        //参数校验
        String accessKey = String.valueOf(params.get("accessKey"));
        String timestamp = String.valueOf(params.get("timestamp"));
        String nonce = String.valueOf(params.get("nonce"));
        String sign = String.valueOf(params.get("sign"));

        //校验用户信息（accessKey）

        InnerResult<User> userResult = innerUserService.selectUserByAccessKey(accessKey);
        if (null == userResult || null == userResult.getData()) {
            return handlerNoAuth(originalResponse);
        }
        User user = userResult.getData();
        //校验接口信息
        InnerResult<InterfaceInfo> interfaceResult = innerInterfaceInfoService.selectInterfaceInfo(uri.getPath(), method.name());
        if (null == interfaceResult || null == interfaceResult.getData()) {
            return handlerNoAuth(originalResponse);
        }
        InterfaceInfo interfaceInfo = interfaceResult.getData();

        log.info("调用的接口:{}", interfaceInfo);
        log.info("接口调用者:{}", user);

        //校验时间戳 和 nonce
        if (!ervifyTimestampAndNonceAreLegal(timestamp, nonce)) {
            return handlerNoAuth(originalResponse);
        }

        //校验sign参数,去除参数中的sign
        params.put("secretKey", user.getSecretKey());
        params.remove("sign");
        String serverSign = SignUtils.createSign(params);
        log.info("服务端 生成的sign:{}", serverSign);
        if (!serverSign.equals(sign)) {
            return handlerNoAuth(originalResponse);
        }

        //响应处理
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {//解决返回体分段传输
                        StringBuffer stringBuffer = new StringBuffer();
                        dataBuffers.forEach(dataBuffer -> {
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);
                            try {
                                stringBuffer.append(new String(content, StandardCharsets.UTF_8));
                            } catch (Exception e) {
                                log.error("--list.add--error", e);
                            }
                        });
                        String result = stringBuffer.toString();
                        InnerResult innerResult;
                        try {
                            innerResult = objectMapper.readValue(result, InnerResult.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        log.info("响应结果:{}", innerResult);
                        //调用接口次数+1
                        innerUserInterfaceInfoService.incrementInterfaceCallCount(interfaceInfo.getId(), user.getId());
                        byte[] uppedContent = new String(result.getBytes(), StandardCharsets.UTF_8).getBytes();
                        originalResponse.getHeaders().setContentLength(uppedContent.length);
                        return bufferFactory.wrap(uppedContent);
                    }));
                }
                return super.writeWith(body);
            }
        };
        //TODO gateway应该需要把校验参数(ak、sk、time、nonce、sign)都去除掉，provider能看到他需要的参数
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     *
     * @return 请求体
     */
    private Map<String, Object> resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        // str = name=rookie&k3=v3&k4=v4&k1=v1&k2=v2，还有可能 str = {"id":1001,"userName":"lisi"}
        String str = bodyRef.get();
        Map<String, Object> map = new HashMap<>(16);
        //JSON结构
        if (str.startsWith(StrPool.DELIM_START) && str.endsWith(StrPool.DELIM_END)) {
            JSONObject jsonObject = JSONObject.parseObject(str);
            map.putAll(jsonObject);
        } else {
            String[] pairs = str.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
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

    /**
     * 包装请求参数返回map形式
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param method   请求方式
     * @return 请求参数集合
     */
    private Map<String, Object> wrapRequestParams(ServerHttpRequest request, ServerHttpResponse response, HttpMethod method) {
        HashMap<String, Object> params = new HashMap<>(4);
        if (HttpMethod.GET.equals(method)) {
            //封装校验参数
            MultiValueMap<String, String> map = request.getQueryParams();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                params.put(entry.getKey(), entry.getValue().get(0));
            }
        } else if (HttpMethod.POST.equals(method)) {
            Map<String, Object> map = resolveBodyFromRequest(request);
            params.putAll(map);
        }
        return params;
    }

    /**
     * 校验timestamp和nonce是否合法
     *
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 校验结果
     */
    private boolean ervifyTimestampAndNonceAreLegal(String timestamp, String nonce) {
        if (null == timestamp || Long.parseLong(timestamp) <= 0L) {
            return false;
        }
        if (null == nonce || Long.parseLong(nonce) <= 0L) {
            return false;
        }
        Long lastTimeStamp = redisCache.getCacheObject(CacheConstants.INTERFACE_INVOKE_NONCE_KEY + nonce);
        //上一次同一个随机数保存时间间隔需要大于nonceMaxCacheTime
        if (null != lastTimeStamp && Long.sum(nonceMaxCacheTime, Long.parseLong(timestamp)) >= lastTimeStamp) {
            return false;
        }
        //当前nonce 存入redis
        redisCache.setCacheObject(CacheConstants.INTERFACE_INVOKE_NONCE_KEY + nonce, lastTimeStamp, nonceMaxCacheTime.intValue(), TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
