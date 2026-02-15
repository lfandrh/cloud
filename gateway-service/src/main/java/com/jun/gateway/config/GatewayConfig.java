package com.jun.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    @Order(1)
    public GlobalFilter requestLogFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().toString();

            if (path.startsWith("/auth") || path.startsWith("/systemManage")) {
                log.info("[Gateway] {} {}", method, path);
            }

            return chain.filter(exchange);
        };
    }

    @Bean
    public GlobalFilter fallbackFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .onErrorResume(throwable -> {
                    log.error("[Gateway] 下游服务异常: {}", throwable.getMessage());
                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE);
                    byte[] bytes = ("服务暂不可用: " + throwable.getMessage()).getBytes();
                    return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
                });
    }
}
