# Product Service - Product Catalog with GraphQL

## Overview

Product Service manages the product catalog with both REST APIs and GraphQL support. It provides product information, inventory management, and flexible querying capabilities through GraphQL.

## Architecture Role

- Product Management: CRUD operations for products
- GraphQL Support: Flexible querying and mutations
- Inventory Tracking: Product stock management
- Event Publishing: Kafka events for product changes
- Service Discovery: Registers with Eureka

## Features

### Core Functionality
- Product CRUD: Create, read, update, delete products
- GraphQL API: Flexible queries and mutations
- Inventory Management: Stock tracking and updates
- Category Management: Product categorization
- Search & Filter: Product search capabilities
- Kafka Events: Publish product events

### APIs (3 REST + GraphQL)
- `POST /api/products` - Create product
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products` - List all products
- `POST /graphql` - GraphQL queries and mutations

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Eureka Server running on port 8761
- Kafka running on port 9092

### Running the Service
```bash
# Navigate to product-service directory
cd product-service

# Build the project
mvn clean install

# Run the service
mvn spring-boot:run
```

### Verification
- Swagger UI: http://localhost:8082/swagger-ui.html
- GraphQL: http://localhost:8082/graphql
- Health Check: http://localhost:8082/actuator/health

## Configuration

### Application Properties
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
      group-id: product-service-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

# GraphQL Configuration
graphql:
  graphiql:
    enabled: true
    path: /graphiql
  schema:
    locations: classpath:graphql/
    file-extensions: .graphqls

# Swagger Configuration
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

## Data Models

### Product Entity
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;
    
    @Min(0)
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### Product Category
```java
public enum ProductCategory {
    ELECTRONICS,
    CLOTHING,
    BOOKS,
    HOME_AND_GARDEN,
    SPORTS,
    FOOD_AND_BEVERAGES,
    OTHER
}
```

## GraphQL Implementation

### Schema Definition
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

### Sample Queries
```graphql
# Get all products
query {
  products {
    id
    name
    price
    category
    stockQuantity
  }
}

# Get product by ID
query {
  product(id: "1") {
    id
    name
    description
    price
    category
    stockQuantity
    createdAt
  }
}

# Search products
query {
  searchProducts(name: "laptop") {
    id
    name
    price
    category
  }
}

# Create product
mutation {
  createProduct(input: {
    name: "MacBook Pro"
    description: "High-performance laptop"
    price: 1299.99
    category: ELECTRONICS
    stockQuantity: 10
  }) {
    id
    name
    price
  }
}
```

## Kafka Events

### Event Publishing
```java
@Service
public class ProductService {
    
    @Autowired
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;
    
    public ProductDto createProduct(CreateProductRequest request) {
        Product product = // ... create product
        
        // Publish event
        ProductEvent event = new ProductEvent(
            "PRODUCT_CREATED",
            product.getId(),
            product.getName(),
            product.getCategory().name()
        );
        kafkaTemplate.send("product-events", event);
        
        return convertToDto(product);
    }
}
```

### Event Schema
```java
public class ProductEvent {
    private String eventType;
    private Long productId;
    private String productName;
    private String category;
    private LocalDateTime timestamp;
    
    // Constructors, getters, setters
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

### GraphQL Monitoring
- Query Performance: Monitor query execution times
- Error Tracking: Track GraphQL errors
- Usage Analytics: Monitor query patterns

## Testing

### REST API Testing
1. Use Swagger UI for manual testing
2. Test CRUD operations
3. Test validation and error handling
4. Test Kafka event publishing

### GraphQL Testing
1. Use GraphiQL interface for testing
2. Test queries and mutations
3. Test error handling
4. Test performance with complex queries

### Integration Testing
1. Test service registration with Eureka
2. Test Kafka event publishing
3. Test GraphQL through API Gateway
4. Test complete product lifecycle

## Learning Objectives

### GraphQL Concepts
- Schema Definition: Understanding GraphQL schema
- Resolvers: Implementing query and mutation resolvers
- Type System: GraphQL type system and validation
- Queries vs Mutations: When to use each

### Spring Boot GraphQL
- GraphQL Java: Spring Boot GraphQL integration
- Schema Location: Where to place GraphQL schemas
- Resolver Implementation: How to implement resolvers
- Error Handling: GraphQL error handling

### Event-Driven Architecture
- Kafka Integration: Publishing events to Kafka
- Event Schema: Designing event schemas
- Event Publishing: When and how to publish events
- Event Consumption: How other services consume events

## Production Considerations

### Database
- Use PostgreSQL or MySQL for production
- Implement proper indexing strategies
- Configure connection pooling

### Caching
- Implement Redis for product caching
- Cache frequently accessed products
- Implement cache invalidation strategies

### Search
- Implement Elasticsearch for product search
- Configure search indexing
- Implement search analytics

### Monitoring
- Set up comprehensive monitoring
- Monitor GraphQL performance
- Track Kafka event publishing

---

**Port**: 8082  
**Swagger UI**: http://localhost:8082/swagger-ui.html  
**GraphQL**: http://localhost:8082/graphql  