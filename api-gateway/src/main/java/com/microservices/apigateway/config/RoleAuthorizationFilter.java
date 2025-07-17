package com.microservices.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RoleAuthorizationFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getPath().toString();
        String method = request.getMethod() != null ? request.getMethod().name() : "";
        String role = request.getHeaders().getFirst("X-User-Role");
        String userId = request.getHeaders().getFirst("X-User-Id");

        // Allow public endpoints
        if (path.startsWith("/api/users/register") || path.startsWith("/api/users/login") ||
            path.startsWith("/swagger") || path.startsWith("/api-docs") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        // Product creation: only ADMIN or MANAGER
        if (path.equals("/api/products") && method.equals("POST")) {
            if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }
        }
        // Product update/delete: only ADMIN or MANAGER
        if (path.matches("/api/products/\\d+") && (method.equals("PUT") || method.equals("DELETE"))) {
            if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }
        }
        // Order creation: only USER
        if (path.equals("/api/orders") && method.equals("POST")) {
            if (!"USER".equals(role)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }
        }
        // Order status update: USER can cancel own, ADMIN can approve/reject any
        if (path.matches("/api/orders/\\d+/status") && method.equals("PUT")) {
            if ("USER".equals(role)) {
                // Only allow if user is owner (ownership check in order service)
                // Allow, but order service must enforce ownership
                return chain.filter(exchange);
            } else if (!"ADMIN".equals(role)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }
        }
        // Order listing: USER (own), ADMIN (all)
        if (path.equals("/api/orders") && method.equals("GET")) {
            if (!"USER".equals(role) && !"ADMIN".equals(role)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }
        }
        // Default: allow
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0; // Run after JwtAuthenticationFilter
    }
} 