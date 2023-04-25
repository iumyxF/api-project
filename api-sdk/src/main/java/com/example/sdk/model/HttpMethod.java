package com.example.sdk.model;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fzy
 * @description:
 * @date 2023/4/20 13:53
 */
public enum HttpMethod {

    /**
     * GET
     */
    GET,

    /**
     * POST
     */
    POST,

    /**
     * PUT
     */
    PUT,

    /**
     * DELETE
     */
    DELETE;

    private static final Map<String, HttpMethod> MAPPINGS = new HashMap(4);

    HttpMethod() {
    }

    @Nullable
    public static HttpMethod resolve(@Nullable String method) {
        return method != null ? MAPPINGS.get(method) : null;
    }

    public boolean matches(String method) {
        return this.name().equals(method.toUpperCase());
    }

    static {
        HttpMethod[] methods = values();
        for (HttpMethod httpMethod : methods) {
            MAPPINGS.put(httpMethod.name(), httpMethod);
        }
    }
}
