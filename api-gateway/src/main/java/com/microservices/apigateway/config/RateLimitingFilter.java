package com.microservices.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    @Value("${rate.limit.requests-per-minute:60}")
    private int requestsPerMinute;

    private static final List<String> EXEMPT_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/actuator",
            "/fallback"
    );

    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        if (isExemptEndpoint(path)) {
            return chain.filter(exchange);
        }

        String clientIp = getClientIp(request);
        if (isRateLimitExceeded(clientIp)) {
            return rateLimitExceeded(exchange.getResponse());
        }

        return chain.filter(exchange);
    }

    private boolean isExemptEndpoint(String path) {
        return EXEMPT_ENDPOINTS.stream().anyMatch(path::startsWith) ||
                path.startsWith("/actuator/") || path.startsWith("/fallback/");
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddress() != null ? 
               request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    private boolean isRateLimitExceeded(String clientIp) {
        LocalDateTime now = LocalDateTime.now();
        RequestCounter counter = requestCounters.computeIfAbsent(clientIp, k -> new RequestCounter());

        if (counter.isExpired(now)) {
            counter.reset(now);
        }

        return !counter.increment();
    }

    private Mono<Void> rateLimitExceeded(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        response.getHeaders().add("X-RateLimit-Remaining", "0");
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -2; // Run after JWT filter but before other filters
    }

    private static class RequestCounter {
        private int count = 0;
        private LocalDateTime windowStart;

        public boolean increment() {
            if (count < 60) { // requestsPerMinute
                count++;
                return true;
            }
            return false;
        }

        public boolean isExpired(LocalDateTime now) {
            return windowStart == null || 
                   now.isAfter(windowStart.plusMinutes(1));
        }

        public void reset(LocalDateTime now) {
            count = 0;
            windowStart = now;
        }
    }
} 