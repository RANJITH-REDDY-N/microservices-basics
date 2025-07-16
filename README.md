# Microservices Basics - Complete Architecture

A comprehensive microservices architecture demonstrating Spring Boot, Eureka, API Gateway, Kafka, GraphQL, and monitoring capabilities.

## Architecture Overview

This project implements a complete microservices ecosystem with:

- 3 Core Services: User, Product, and Order services
- Infrastructure: Eureka Service Discovery, API Gateway, Kafka
- Communication: REST APIs, GraphQL, and Kafka events
- Security: JWT authentication
- Monitoring: Health checks and metrics
- Documentation: Swagger/OpenAPI

## Services

| Service | Port | Description |
|---------|------|-------------|
| Eureka Server | 8761 | Service discovery and registration |
| API Gateway | 8080 | Centralized routing and load balancing |
| User Service | 8081 | User management and authentication |
| Product Service | 8082 | Product catalog with GraphQL |
| Order Service | 8083 | Order processing and management |
| Kafka | 9092 | Message broker for async communication |
| Kafka UI | 9080 | Kafka monitoring UI |

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker (for Kafka)

### 1. Start Infrastructure
```bash
# Start Kafka
cd kafka-setup
docker-compose up -d
```

### 2. Start Services (in order)
```bash
# Start Eureka Server
cd eureka-server
mvn spring-boot:run

# Start API Gateway
cd api-gateway
mvn spring-boot:run

# Start User Service
cd user-service
mvn spring-boot:run

# Start Product Service
cd product-service
mvn spring-boot:run

# Start Order Service
cd order-service
mvn spring-boot:run
```

### 3. Verify Services

- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080
- Swagger UI:
  - User Service: http://localhost:8081/swagger-ui.html
  - Product Service: http://localhost:8082/swagger-ui.html
  - Order Service: http://localhost:8083/swagger-ui.html
- GraphQL Playground (Product Service): http://localhost:8082/graphiql
- GraphQL Endpoint (Product Service): http://localhost:8082/graphql
- Kafka Broker: localhost:9092
- Kafka UI: http://localhost:9080

## Project Structure

```
MicroservicesBasics/
├── user-service/           # User management (existing)
├── product-service/        # Product catalog with GraphQL
├── order-service/         # Order processing
├── eureka-server/         # Service discovery
├── api-gateway/          # API Gateway with load balancing
├── kafka-setup/          # Kafka configuration
├── docs/                 # Project documentation
├── PROJECT_ARCHITECTURE.md
├── IMPLEMENTATION_GUIDE.md
└── README.md
```

## Communication Flow

### Synchronous Communication
```
Client → API Gateway → Service (User/Product/Order)
```

### Asynchronous Communication
```
User Service → Kafka → Order Service
Product Service → Kafka → Order Service
Order Service → Kafka → User Service
```

### GraphQL Communication
```
Client → API Gateway → Product Service (GraphQL)
```

## Technology Stack

### Backend
- Framework: Spring Boot 3.2.0
- Service Discovery: Spring Cloud Netflix Eureka
- API Gateway: Spring Cloud Gateway
- Messaging: Apache Kafka
- GraphQL: GraphQL Java
- Security: Spring Security with JWT
- Database: H2 (in-memory)
- Documentation: Swagger/OpenAPI 3

### Monitoring
- Health Checks: Spring Boot Actuator
- Metrics: Micrometer
- Service Discovery: Eureka Dashboard

## Service APIs

### User Service (3 APIs)
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication
- `GET /api/users/profile` - Get user profile

### Product Service (3 APIs + GraphQL)
- `POST /api/products` - Create product
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products` - List all products
- `POST /graphql` - GraphQL queries and mutations

### Order Service (3 APIs)
- `POST /api/orders` - Create order
- `GET /api/orders/{id}` - Get order by ID
- `PUT /api/orders/{id}/status` - Update order status

## Security

### Authentication Flow
1. Client sends login request to API Gateway
2. API Gateway routes to User Service
3. User Service validates credentials and returns JWT
4. Client includes JWT in subsequent requests
5. API Gateway validates JWT before routing to services

### Authorization
- Public Endpoints: Registration, login, health checks
- Protected Endpoints: All other endpoints require valid JWT
- Role-based Access: Different roles for different operations

## Monitoring & Health

### Health Checks
- Actuator Endpoints: `/actuator/health` for each service
- Eureka Health: Automatic health monitoring
- Kafka Health: Connection status monitoring

### Metrics
- Application Metrics: Request counts, response times
- Business Metrics: Orders created, products sold
- Infrastructure Metrics: Memory usage, CPU usage

## Testing

### API Testing
- Swagger UI: Interactive API documentation and testing
- Postman: Automated API testing
- GraphQL Playground: GraphQL query testing

### Load Testing
- JMeter: Performance testing
- Load Balancer: Client-side load balancing

## Learning Objectives

### Microservices Concepts
- Service decomposition and boundaries
- Service discovery and registration
- API Gateway patterns
- Event-driven architecture

### Spring Cloud
- Eureka service discovery
- Spring Cloud Gateway
- Spring Cloud Stream (Kafka)

### GraphQL
- Schema definition
- Resolvers and data fetchers
- Queries and mutations

### Event-Driven Architecture
- Kafka producer/consumer patterns
- Event sourcing concepts
- Saga pattern implementation

### Monitoring & Observability
- Health checks and metrics
- Service monitoring
- Distributed tracing concepts

## Implementation Phases

### Phase 1: Infrastructure Setup
1. Eureka Server - Service discovery and registration
2. API Gateway - Centralized routing with load balancing
3. Kafka Setup - Message broker configuration

### Phase 2: Core Services
4. Product Service - Product management with GraphQL
5. Order Service - Order processing
6. Update User Service - Add Kafka integration

### Phase 3: Integration & Communication
7. Kafka Events - Service-to-service communication
8. GraphQL Integration - Product service queries
9. API Gateway Configuration - Route all services

### Phase 4: Monitoring & Testing
10. Health Checks - Actuator endpoints
11. Load Balancer - Client-side load balancing
12. Testing - End-to-end testing with Swagger

## Documentation

- All documentation is now consolidated in this README.

## Success Criteria

### Functional Requirements
- All services register with Eureka
- API Gateway routes requests correctly
- Kafka events are published and consumed
- GraphQL queries work properly
- JWT authentication works end-to-end
- Health checks are accessible

### Non-Functional Requirements
- Services are loosely coupled
- System is scalable
- Monitoring is in place
- Documentation is comprehensive
- Testing coverage is adequate

## Contributing

1. Follow the implementation phases in order
2. Test each service individually before integration
3. Use Swagger UI for API testing
4. Monitor Eureka dashboard for service health
5. Check Kafka events for async communication

## License

This project is for educational purposes to learn microservices architecture concepts.

---

Start with Phase 1: Follow the [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md) to build the complete microservices ecosystem step by step. 