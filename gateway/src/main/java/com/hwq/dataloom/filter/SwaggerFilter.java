package com.hwq.dataloom.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author HWQ
 * @date 2024/8/16 10:39
 * @description
 */
@Component
public class SwaggerFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 处理knife4j的uri
        String path = exchange.getRequest().getURI().getPath();
        if (path.contains("/v3/api-docs")) {
            String newPath = path.substring(0, path.length() - "/default".length());
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate().path(newPath).build())
                    .build();
            return chain.filter(modifiedExchange);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
