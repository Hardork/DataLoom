package com.hwq.dataloom.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author HWQ
 * @date 2024/8/28 15:12
 * @description 全局请求过滤器 - 打印日志
 */
@Component
@Slf4j
public class RequestLogFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();

        String traceId = UUID.randomUUID().toString();

        long startTime = System.currentTimeMillis();
        MDC.put("traceId", traceId);

        log.info("请求URI: {}", request.getURI());
        log.info("请求类型: {}", method);
        log.info("请求头: {}", request.getHeaders());

        if (method == HttpMethod.GET) {
            log.info("请求参数: {}", request.getQueryParams());
        }

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info("响应时间：{} ms", duration);
        }));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
