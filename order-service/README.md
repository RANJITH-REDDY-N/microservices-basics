# Order Service - Order Processing

## Overview

Order Service handles order processing and management. It integrates with User Service and Product Service through Kafka events, manages order lifecycle, and provides order tracking capabilities.

## Architecture Role

- Order Management: CRUD operations for orders
- Event Consumption: Consumes events from User and Product services
- Order Processing: Handles order creation, updates, and status changes
- Integration: Coordinates with User and Product services
- Event Publishing: Publishes order events for other services

## Features

### Core Functionality
- Order CRUD: Create, read, update, delete orders
- Order Status Management: Track order status (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- Event Consumption: Consume user and product events
- Event Publishing: Publish order events
- Order History: Track order history and changes

### APIs (3 endpoints)
- `POST /api/orders` - Create order
- `GET /api/orders/{id}` - Get order by ID
- `PUT /api/orders/{id}/status` - Update order status

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Eureka Server running on port 8761
- Kafka running on port 9092
- User Service running on port 8081
- Product Service running on port 8082

### Running the Service
```bash
# Navigate to order-service directory
cd order-service

# Build the project
mvn clean install

# Run the service
mvn spring-boot:run
```

### Verification
- Swagger UI: http://localhost:8083/swagger-ui.html
- Health Check: http://localhost:8083/actuator/health

## Configuration

### Application Properties
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
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
  
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

# Swagger Configuration
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

## Data Models

### Order Entity
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String username;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### Order Item Entity
```java
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private BigDecimal unitPrice;
    
    @Column(nullable = false)
    private BigDecimal totalPrice;
}
```

### Order Status
```java
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
```

## Kafka Events

### Event Consumption
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

### Event Publishing
```java
@Service
public class OrderService {
    
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    public OrderDto createOrder(CreateOrderRequest request) {
        Order order = // ... create order
        
        // Publish event
        OrderEvent event = new OrderEvent(
            "ORDER_CREATED",
            order.getId(),
            order.getUserId(),
            order.getTotalAmount()
        );
        kafkaTemplate.send("order-events", event);
        
        return convertToDto(order);
    }
}
```

### Event Schemas
```java
public class UserEvent {
    private String eventType;
    private Long userId;
    private String username;
    private String email;
    private LocalDateTime timestamp;
}

public class ProductEvent {
    private String eventType;
    private Long productId;
    private String productName;
    private String category;
    private LocalDateTime timestamp;
}

public class OrderEvent {
    private String eventType;
    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;
}
```

## Dependencies

### Core Dependencies
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
```

### Database & Documentation
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

## Monitoring & Health

### Health Endpoints
- `/actuator/health` - Service health status
- `/actuator/info` - Service information
- `/actuator/metrics` - Service metrics

### Kafka Monitoring
- Consumer Lag: Monitor consumer lag
- Event Processing: Track event processing times
- Error Handling: Monitor failed event processing

## Testing

### REST API Testing
1. Use Swagger UI for manual testing
2. Test order CRUD operations
3. Test order status updates
4. Test validation and error handling

### Event Testing
1. Test event consumption from User Service
2. Test event consumption from Product Service
3. Test event publishing to Kafka
4. Test complete event flow

### Integration Testing
1. Test service registration with Eureka
2. Test Kafka event flow
3. Test order creation with user and product events
4. Test complete order lifecycle

## Learning Objectives

### Event-Driven Architecture
- Event Consumption: How to consume events from Kafka
- Event Publishing: How to publish events to Kafka
- Event Schema Design: Designing event schemas
- Event Flow: Understanding event flow between services

### Order Processing
- Order Lifecycle: Managing order status transitions
- Data Consistency: Ensuring data consistency across services
- Error Handling: Handling failures in order processing
- Integration: Integrating with other services

### Kafka Integration
- Consumer Groups: Understanding consumer groups
- Offset Management: Managing consumer offsets
- Error Handling: Handling Kafka errors
- Performance: Optimizing Kafka performance

## Order Processing Flow

### Order Creation
1. Client Request: Client sends order creation request
2. Validation: Validate order items and user
3. Order Creation: Create order in database
4. Event Publishing: Publish ORDER_CREATED event
5. Response: Return order details to client

### Order Status Updates
1. Status Update: Update order status
2. Validation: Validate status transition
3. Event Publishing: Publish ORDER_UPDATED event
4. Notification: Notify relevant services

### Event Processing
1. Event Reception: Receive events from Kafka
2. Event Processing: Process event based on type
3. Data Update: Update local data if needed
4. Error Handling: Handle processing errors

## Production Considerations

### Database
- Use PostgreSQL or MySQL for production
- Implement proper indexing strategies
- Configure connection pooling

### Event Sourcing
- Consider event sourcing for order history
- Implement event store for audit trail
- Configure event replay capabilities

### Saga Pattern
- Implement saga pattern for distributed transactions
- Handle compensation logic for failures
- Ensure eventual consistency

### Monitoring
- Set up comprehensive monitoring
- Monitor order processing performance
- Track event processing metrics

---

**Port**: 8083  
**Swagger UI**: http://localhost:8083/swagger-ui.html 