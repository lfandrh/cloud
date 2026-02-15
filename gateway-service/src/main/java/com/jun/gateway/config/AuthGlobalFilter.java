package com.jun.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthGlobalFilter implements GlobalFilter {

    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/logout",
            "/auth/sendCaptcha",
            "/auth/verifyCaptcha",
            "/actuator"
    );

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isExcludePath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        String token = extractToken(authHeader);

        if (token == null || token.isEmpty()) {
            return unauthorized(exchange.getResponse(), "未登录");
        }

        String satokenKey = "satoken:login:token:" + token;

        return redisTemplate.hasKey(satokenKey)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return redisTemplate.opsForValue().get(satokenKey)
                                .flatMap(userId -> buildRequestWithUserInfo(exchange, chain, token, userId));
                    } else {
                        return unauthorized(exchange.getResponse(), "Token 无效");
                    }
                })
                .onErrorResume(e -> {
                    log.error("认证错误: {}", e.getMessage());
                    return unauthorized(exchange.getResponse(), "认证失败");
                });
    }

    private Mono<Void> buildRequestWithUserInfo(ServerWebExchange exchange, GatewayFilterChain chain, String token, String userId) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder requestBuilder = request.mutate()
                .header("X-User-Id", userId)
                .header("satoken", token);

        String sessionKey = "satoken:login:session:" + token;
        
        return redisTemplate.opsForValue().get(sessionKey)
                .defaultIfEmpty("{}")
                .flatMap(sessionData -> {
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> sessionMap = objectMapper.readValue(sessionData, Map.class);
                        
                        if (sessionMap != null) {
                            Object username = sessionMap.get("username");
                            if (username != null) {
                                requestBuilder.header("X-Username", username.toString());
                            }
                            
                            Object roles = sessionMap.get("roles");
                            if (roles != null) {
                                if (roles instanceof List) {
                                    @SuppressWarnings("unchecked")
                                    List<String> roleList = (List<String>) roles;
                                    requestBuilder.header("X-User-Roles", String.join(",", roleList));
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("解析 session 失败: {}", e.getMessage());
                    }
                    
                    ServerHttpRequest mutatedRequest = requestBuilder.build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    private boolean isExcludePath(String path) {
        return EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\":401,\"message\":\"" + message + "\"}";
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }
}
