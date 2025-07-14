# API Gateway - Centralized Routing

## Overview

API Gateway acts as the single entry point for all client requests, providing centralized routing, load balancing, authentication, and cross-cutting concerns for the microservices architecture.

## Architecture Role

- Single Entry Point: All client requests go through the gateway
- Request Routing: Routes requests to appropriate microservices
- Load Balancing: Distributes load across service instances
- Authentication: JWT token validation
- Rate Limiting: Prevents abuse and ensures fair usage
- CORS: Handles cross-origin requests

## Features

### Core Functionality
- Request Routing: Route requests to appropriate services
- Load Balancing: Client-side load balancing using Eureka
- Authentication: JWT token validation and forwarding
- Rate Limiting: Request throttling and abuse prevention
- CORS Support: Cross-origin resource sharing
- Circuit Breaker: Fault tolerance and resilience

### Routes Configuration
- `/api/auth/**` → User Service
- `/api/users/**` → User Service
- `/api/products/**` → Product Service
- `/api/orders/**` → Order Service
- `/graphql` → Product Service (GraphQL)

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Eureka Server running on port 8761

### Running the Service
```bash
# Navigate to api-gateway directory
cd api-gateway

# Build the project
mvn clean install

# Run the service
mvn spring-boot:run
```

### Verification
- Gateway: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- Service Routes: Test routing to different services

## Configuration

### Application Properties
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/auth/**, /api/users/**
          filters:
            - name: CircuitBreaker
              args:
                name: user-service-circuit-breaker
                fallbackUri: forward:/fallback/user-service
        
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - name: CircuitBreaker
              args:
                name: product-service-circuit-breaker
                fallbackUri: forward:/fallback/product-service
        
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - name: CircuitBreaker
              args:
                name: order-service-circuit-breaker
                fallbackUri: forward:/fallback/order-service
        
        - id: graphql
          uri: lb://product-service
          predicates:
            - Path=/graphql
          filters:
            - name: CircuitBreaker
              args:
                name: graphql-circuit-breaker
                fallbackUri: forward:/fallback/graphql

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Key Features
- Load Balancing: `lb://` prefix for client-side load balancing
- Circuit Breaker: Fault tolerance for service failures
- Service Discovery: Automatic service discovery via Eureka
- Path-based Routing: Route based on request path

## Security Configuration

### JWT Authentication
```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Skip authentication for public endpoints
        if (isPublicEndpoint(request.getPath().toString())) {
            return chain.filter(exchange);
        }
        
        // Extract and validate JWT token
        String token = extractToken(request);
        if (validateToken(token)) {
            // Add user info to headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", getUserIdFromToken(token))
                .header("X-User-Role", getUserRoleFromToken(token))
                .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }
        
        return unauthorized(exchange);
    }
}
```

### Public Endpoints
- `/api/auth/register` - User registration
- `/api/auth/login` - User authentication
- `/actuator/**` - Health checks and metrics
- `/fallback/**` - Circuit breaker fallbacks

## Load Balancing

### Client-Side Load Balancing
- Eureka Integration: Automatic service discovery
- Round Robin: Default load balancing strategy
- Health Checks: Only healthy instances receive traffic
- Instance Selection: Automatic instance selection

### Load Balancer Configuration
```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
      health-check:
        initial-delay: 0
        interval: 25s
```

## Request Flow

### Authentication Flow
1. Client Request: Client sends request with JWT token
2. Gateway Filter: JWT filter validates token
3. Service Discovery: Gateway finds service via Eureka
4. Load Balancing: Gateway selects service instance
5. Request Forwarding: Gateway forwards request to service
6. Response: Service response returned to client

### Error Handling
1. Circuit Breaker: Prevents cascading failures
2. Fallback: Returns fallback response when service is down
3. Timeout: Configurable request timeouts
4. Retry: Automatic retry for transient failures

## Dependencies

### Core Dependencies
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### JWT Dependencies
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

## Monitoring & Health

### Health Endpoints
- `/actuator/health` - Gateway health status
- `/actuator/gateway/routes` - Route information
- `/actuator/metrics` - Gateway metrics

### Circuit Breaker Monitoring
- Success Rate: Percentage of successful requests
- Failure Rate: Percentage of failed requests
- Response Time: Average response time
- Open/Closed State: Circuit breaker state

## Rate Limiting

### Configuration
```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - name: RequestRateLimiter
          args:
            redis-rate-limiter.replenishRate: 10
            redis-rate-limiter.burstCapacity: 20
```

### Rate Limiting Features
- Per User: Rate limiting per user/IP
- Per Service: Different limits for different services
- Burst Handling: Handle traffic spikes
- Custom Headers: Rate limit information in headers

## Testing

### Manual Testing
1. Start API Gateway
2. Test routing to different services
3. Test authentication with JWT tokens
4. Test load balancing with multiple instances
5. Test circuit breaker with service failures

### Integration Testing
1. Test complete request flow
2. Test authentication flow
3. Test load balancing
4. Test circuit breaker scenarios
5. Test rate limiting

## Learning Objectives

### API Gateway Concepts
- Single Entry Point: Centralized request handling
- Request Routing: Path-based routing
- Load Balancing: Client-side load balancing
- Authentication: Token validation and forwarding

### Spring Cloud Gateway
- Route Configuration: YAML-based route configuration
- Filter Chains: Request/response filtering
- Circuit Breaker: Fault tolerance patterns
- Service Discovery: Eureka integration

### Security Patterns
- JWT Validation: Token-based authentication
- Header Forwarding: Passing user context
- CORS Handling: Cross-origin requests
- Rate Limiting: Abuse prevention

## Production Considerations

### High Availability
- Deploy multiple gateway instances
- Configure proper load balancing
- Implement health checks

### Security
- Enable SSL/TLS for all communications
- Implement proper authentication
- Configure rate limiting appropriately

### Monitoring
- Set up comprehensive monitoring
- Configure alerts for failures
- Monitor performance metrics

---

**Port**: 8080  
**Health Check**: http://localhost:8080/actuator/health