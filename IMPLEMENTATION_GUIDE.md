# Microservices Implementation Guide

## Overview

This guide provides step-by-step instructions to implement a complete microservices architecture with Spring Boot, Eureka, API Gateway, Kafka, and GraphQL.

## Implementation Phases

### Phase 1: Infrastructure Setup

#### Step 1: Eureka Server
Create the service discovery server that all other services will register with.

**Location**: `eureka-server/`

**Key Components**:
- Spring Cloud Netflix Eureka Server
- Service registration and discovery
- Health monitoring dashboard
- Client-side load balancing support

**Configuration**:
```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    wait-time-in-ms-when-sync-empty: 0
    enable-self-preservation: true
    eviction-interval-timer-in-ms: 1000
```

**Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### Step 2: API Gateway
Create the centralized gateway for routing requests to appropriate services.

**Location**: `api-gateway/`

**Key Components**:
- Spring Cloud Gateway
- JWT authentication filter
- Load balancing
- Circuit breaker integration
- Rate limiting

**Configuration**:
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
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
        - id: graphql
          uri: lb://product-service
          predicates:
            - Path=/graphql

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

**Dependencies**:
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
```

#### Step 3: Kafka Setup
Configure Kafka for asynchronous communication between services.

**Location**: `kafka-setup/`

**Docker Compose**:
```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    volumes:
      - zookeeper-data:/var/lib/zookeeper/data
      - zookeeper-logs:/var/lib/zookeeper/log

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
    volumes:
      - kafka-data:/var/lib/kafka/data

volumes:
  zookeeper-data:
  zookeeper-logs:
  kafka-data:
```

### Phase 2: Core Services

#### Step 4: Product Service
Create the product catalog service with GraphQL support.

**Location**: `product-service/`

**Key Components**:
- Product CRUD operations
- GraphQL schema and resolvers
- Kafka event publishing
- Swagger documentation

**Configuration**:
```yaml
server:
  port: 8082

spring:
  application:
    name: product-service
  datasource:
    url: jdbc:h2:mem:productdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

graphql:
  graphiql:
    enabled: true
    path: /graphiql
  schema:
    locations: classpath:graphql/
    file-extensions: .graphqls
```

**Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

#### Step 5: Order Service
Create the order processing service that integrates with User and Product services.

**Location**: `order-service/`

**Key Components**:
- Order CRUD operations
- Order status management
- Kafka event consumption
- Integration with User and Product services

**Configuration**:
```yaml
server:
  port: 8083

spring:
  application:
    name: order-service
  datasource:
    url: jdbc:h2:mem:orderdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: order-service-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

**Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

#### Step 6: Update User Service
Add Kafka integration to the existing User Service.

**Key Updates**:
- Add Kafka producer for user events
- Publish events on user creation, update, deletion
- Configure Kafka properties

**Configuration Addition**:
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

**Dependencies Addition**:
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### Phase 3: Integration & Communication

#### Step 7: Kafka Events
Implement event publishing and consumption across all services.

**User Service Events**:
```java
@Component
public class KafkaProducerService {
    
    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;
    
    public void publishUserCreated(User user) {
        UserEvent event = new UserEvent("USER_CREATED", user.getId(), user.getUsername(), user.getEmail());
        kafkaTemplate.send("user-events", event);
    }
    
    public void publishUserUpdated(User user) {
        UserEvent event = new UserEvent("USER_UPDATED", user.getId(), user.getUsername(), user.getEmail());
        kafkaTemplate.send("user-events", event);
    }
    
    public void publishUserDeleted(Long userId, String username) {
        UserEvent event = new UserEvent("USER_DELETED", userId, username, null);
        kafkaTemplate.send("user-events", event);
    }
}
```

**Product Service Events**:
```java
@Service
public class ProductService {
    
    @Autowired
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;
    
    public ProductDto createProduct(CreateProductRequest request) {
        Product product = // ... create product
        
        ProductEvent event = new ProductEvent("PRODUCT_CREATED", product.getId(), product.getName(), product.getCategory().name());
        kafkaTemplate.send("product-events", event);
        
        return convertToDto(product);
    }
}
```

**Order Service Event Consumption**:
```java
@Component
public class KafkaConsumer {
    
    @KafkaListener(topics = "user-events", groupId = "order-service-group")
    public void handleUserEvent(UserEvent event) {
        switch (event.getEventType()) {
            case "USER_CREATED":
                handleUserCreated(event);
                break;
            case "USER_UPDATED":
                handleUserUpdated(event);
                break;
            case "USER_DELETED":
                handleUserDeleted(event);
                break;
        }
    }
    
    @KafkaListener(topics = "product-events", groupId = "order-service-group")
    public void handleProductEvent(ProductEvent event) {
        switch (event.getEventType()) {
            case "PRODUCT_CREATED":
                handleProductCreated(event);
                break;
            case "PRODUCT_UPDATED":
                handleProductUpdated(event);
                break;
            case "PRODUCT_DELETED":
                handleProductDeleted(event);
                break;
        }
    }
}
```

#### Step 8: GraphQL Integration
Implement GraphQL schema and resolvers for Product Service.

**Schema Definition** (`src/main/resources/graphql/schema.graphqls`):
```graphql
type Product {
    id: ID!
    name: String!
    description: String
    price: Float!
    category: ProductCategory!
    stockQuantity: Int!
    createdAt: String!
    updatedAt: String!
}

enum ProductCategory {
    ELECTRONICS
    CLOTHING
    BOOKS
    HOME_AND_GARDEN
    SPORTS
    FOOD_AND_BEVERAGES
    OTHER
}

input CreateProductInput {
    name: String!
    description: String
    price: Float!
    category: ProductCategory!
    stockQuantity: Int!
}

input UpdateProductInput {
    name: String
    description: String
    price: Float
    category: ProductCategory
    stockQuantity: Int
}

type Query {
    products: [Product!]!
    product(id: ID!): Product
    productsByCategory(category: ProductCategory!): [Product!]!
    searchProducts(name: String!): [Product!]!
}

type Mutation {
    createProduct(input: CreateProductInput!): Product!
    updateProduct(id: ID!, input: UpdateProductInput!): Product!
    deleteProduct(id: ID!): Boolean!
    updateStock(id: ID!, quantity: Int!): Product!
}
```

**Resolver Implementation**:
```java
@Component
public class ProductQueryResolver implements GraphQLQueryResolver {
    
    @Autowired
    private ProductService productService;
    
    public List<Product> products() {
        return productService.getAllProducts();
    }
    
    public Product product(Long id) {
        return productService.getProductById(id);
    }
    
    public List<Product> productsByCategory(ProductCategory category) {
        return productService.getProductsByCategory(category);
    }
    
    public List<Product> searchProducts(String name) {
        return productService.searchProductsByName(name);
    }
}

@Component
public class ProductMutationResolver implements GraphQLMutationResolver {
    
    @Autowired
    private ProductService productService;
    
    public Product createProduct(CreateProductInput input) {
        return productService.createProduct(input);
    }
    
    public Product updateProduct(Long id, UpdateProductInput input) {
        return productService.updateProduct(id, input);
    }
    
    public Boolean deleteProduct(Long id) {
        return productService.deleteProduct(id);
    }
    
    public Product updateStock(Long id, Integer quantity) {
        return productService.updateStock(id, quantity);
    }
}
```

#### Step 9: API Gateway Configuration
Configure API Gateway to route all services and handle authentication.

**JWT Authentication Filter**:
```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        if (isPublicEndpoint(request.getPath().toString())) {
            return chain.filter(exchange);
        }
        
        String token = extractToken(request);
        if (validateToken(token)) {
            ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", getUserIdFromToken(token))
                .header("X-User-Role", getUserRoleFromToken(token))
                .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }
        
        return unauthorized(exchange);
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/") || 
               path.startsWith("/actuator/") || 
               path.startsWith("/fallback/");
    }
}
```

### Phase 4: Monitoring & Testing

#### Step 10: Health Checks
Implement comprehensive health checks for all services.

**Actuator Configuration**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

**Custom Health Indicators**:
```java
@Component
public class KafkaHealthIndicator implements HealthIndicator {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Override
    public Health health() {
        try {
            kafkaTemplate.send("health-check", "ping").get(5, TimeUnit.SECONDS);
            return Health.up().withDetail("kafka", "connected").build();
        } catch (Exception e) {
            return Health.down().withDetail("kafka", "disconnected").withException(e).build();
        }
    }
}
```

#### Step 11: Load Balancer
Configure client-side load balancing for all services.

**Load Balancer Configuration**:
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

**Service Discovery Integration**:
```java
@LoadBalanced
@Bean
public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
}
```

#### Step 12: Testing
Implement comprehensive testing for all services.

**Unit Tests**:
```java
@SpringBootTest
class ProductServiceTest {
    
    @Autowired
    private ProductService productService;
    
    @MockBean
    private ProductRepository productRepository;
    
    @Test
    void createProduct_ShouldReturnProduct() {
        // Test implementation
    }
}
```

**Integration Tests**:
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        // Test implementation
    }
}
```

**End-to-End Tests**:
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroservicesIntegrationTest {
    
    @Test
    void completeOrderFlow_ShouldWork() {
        // Test complete order creation flow
    }
}
```

## Testing Strategy

### Manual Testing
1. Start all services in order
2. Verify Eureka dashboard shows all services
3. Test API Gateway routing
4. Test GraphQL queries
5. Test Kafka event flow
6. Test JWT authentication

### Automated Testing
1. Unit tests for each service
2. Integration tests for API endpoints
3. End-to-end tests for complete flows
4. Load testing for performance validation

## Deployment

### Development Environment
- All services run locally
- Kafka runs in Docker
- H2 in-memory databases
- Swagger UI for API testing

### Production Considerations
- Containerize all services
- Use external databases (PostgreSQL/MySQL)
- Configure proper monitoring
- Implement security measures
- Set up CI/CD pipelines

## Success Verification

### Functional Verification
- All services register with Eureka
- API Gateway routes requests correctly
- Kafka events are published and consumed
- GraphQL queries work properly
- JWT authentication works end-to-end
- Health checks are accessible

### Performance Verification
- Services respond within acceptable time limits
- Load balancing works correctly
- Kafka events are processed efficiently
- Memory and CPU usage are reasonable

---

This implementation guide provides a complete roadmap for building a microservices architecture with Spring Boot, covering all aspects from infrastructure setup to production deployment. 