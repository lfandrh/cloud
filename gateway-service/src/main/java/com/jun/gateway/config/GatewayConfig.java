package com.jun.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    @Order(1)
    public GlobalFilter requestLogFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod() == null ? "UNKNOWN" : exchange.getRequest().getMethod().name();
            log.info("[Gateway] {} {}", method, path);
            return chain.filter(exchange);
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
