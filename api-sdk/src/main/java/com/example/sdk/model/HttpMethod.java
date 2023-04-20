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

    private static final Map<String, HttpMethod> mappings = new HashMap(16);

    private HttpMethod() {
    }

    @Nullable
    public static HttpMethod resolve(@Nullable String method) {
        return method != null ? (HttpMethod) mappings.get(method) : null;
    }

    public boolean matches(String method) {
        return this.name().equals(method.toUpperCase());
    }

    static {
        HttpMethod[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            HttpMethod httpMethod = var0[var2];
            mappings.put(httpMethod.name(), httpMethod);
        }
    }
}
