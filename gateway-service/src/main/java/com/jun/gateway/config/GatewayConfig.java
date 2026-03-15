package com.jun.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    @Order(1)
    public GlobalFilter requestLogFilter() {
        return (exchange, chain) -> {
            long start = System.currentTimeMillis();
            String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString().replace("-", "");
            }
            ServerHttpRequest request = exchange.getRequest().mutate().header("X-Trace-Id", traceId).build();
            String path = request.getURI().getPath();
            String method = request.getMethod() == null ? "UNKNOWN" : request.getMethod().name();
            String userId = request.getHeaders().getFirst("X-User-Id");
            if (userId == null || userId.isBlank()) {
                userId = "-";
            }
            String finalUserId = userId;
            String finalTraceId = traceId;
            return chain.filter(exchange.mutate().request(request).build()).doFinally(signalType -> {
                long durationMs = System.currentTimeMillis() - start;
                Integer statusCode = exchange.getResponse().getStatusCode() == null
                        ? null
                        : exchange.getResponse().getStatusCode().value();
                log.info("gateway_access traceId={} userId={} method={} uri={} status={} durationMs={}",
                        finalTraceId, finalUserId, method, path, statusCode, durationMs);
            });
        };
    }

    @Bean
    public GlobalFilter fallbackFilter() {
        return (exchange, chain) -> chain.filter(exchange).onErrorResume(throwable -> {
            log.error("[Gateway] Downstream service error: {}", throwable.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = "{\"code\":503,\"msg\":\"Service unavailable\",\"data\":null}";
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
        });
    }
}
