package com.jun.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter {

    private static final List<String> EXCLUDE_EXACT_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/loginByPhone",
            "/auth/refreshToken",
            "/auth/sendCaptcha",
            "/auth/verifyCaptcha",
            "/route/getConstantRoutes",
            "/route/isRouteExist"
    );

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (HttpMethod.OPTIONS.equals(request.getMethod()) || isExcludePath(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(request.getHeaders().getFirst("Authorization"));
        if (token == null || token.isEmpty()) {
            return unauthorized(exchange.getResponse(), "Unauthorized");
        }

        String tokenKey = "satoken:login:token:" + token;
        return redisTemplate.hasKey(tokenKey)
                .flatMap(exists -> {
                    if (!Boolean.TRUE.equals(exists)) {
                        return unauthorized(exchange.getResponse(), "Invalid token");
                    }
                    return redisTemplate.opsForValue().get(tokenKey)
                            .flatMap(userId -> buildRequestWithUserInfo(exchange, chain, token, userId));
                })
                .onErrorResume(e -> {
                    log.error("Auth error: {}", e.getMessage());
                    return unauthorized(exchange.getResponse(), "Authentication failed");
                });
    }

    private Mono<Void> buildRequestWithUserInfo(ServerWebExchange exchange, GatewayFilterChain chain, String token, String userId) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder requestBuilder = request.mutate()
                .header("X-User-Id", userId)
                .header("satoken", token);

        // Try both possible session key forms. If not found, forward with user id only.
        String sessionKeyByToken = "satoken:login:session:" + token;
        String sessionKeyByUser = "satoken:login:session:" + userId;

        return redisTemplate.opsForValue().get(sessionKeyByToken)
                .switchIfEmpty(redisTemplate.opsForValue().get(sessionKeyByUser))
                .defaultIfEmpty("{}")
                .flatMap(sessionData -> {
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> sessionMap = objectMapper.readValue(sessionData, Map.class);
                        Object username = sessionMap.get("username");
                        if (username != null) {
                            requestBuilder.header("X-Username", String.valueOf(username));
                        }
                        Object roles = sessionMap.get("roles");
                        if (roles instanceof List<?>) {
                            @SuppressWarnings("unchecked")
                            List<Object> roleList = (List<Object>) roles;
                            requestBuilder.header("X-User-Roles", String.join(",", roleList.stream().map(String::valueOf).toList()));
                        }
                    } catch (Exception e) {
                        log.warn("Parse session failed: {}", e.getMessage());
                    }

                    ServerHttpRequest mutatedRequest = requestBuilder.build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    private boolean isExcludePath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if ("/actuator".equals(path) || path.startsWith("/actuator/")) {
            return true;
        }
        return EXCLUDE_EXACT_PATHS.contains(path);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"msg\":\"" + message + "\",\"data\":null}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}
