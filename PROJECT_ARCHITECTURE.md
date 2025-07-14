# Microservices Architecture - Complete Guide

## Project Overview

This project demonstrates a complete microservices architecture with Spring Boot, featuring service discovery, API gateway, Kafka messaging, GraphQL, and monitoring capabilities.

## Architecture Components

### Core Services
- User Service (Port: 8081) - User management and authentication
- Product Service (Port: 8082) - Product catalog with GraphQL
- Order Service (Port: 8083) - Order processing and management

### Infrastructure Services
- Eureka Server (Port: 8761) - Service discovery and registration
- API Gateway (Port: 8080) - Centralized routing and load balancing
- Kafka (Port: 9092) - Message broker for async communication

### Communication Patterns
- Synchronous: REST APIs through API Gateway
- Asynchronous: Kafka events between services
- GraphQL: Product service for flexible queries

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

## Service Responsibilities

### User Service (Port: 8081)
**Responsibilities:**
- User registration and authentication
- JWT token generation and validation
- User profile management
- Role-based access control

**APIs (3 endpoints):**
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication
- `GET /api/users/profile` - Get user profile

**Kafka Events:**
- `USER_CREATED` - When new user is registered
- `USER_UPDATED` - When user profile is updated
- `USER_DELETED` - When user is deleted

### Product Service (Port: 8082)
**Responsibilities:**
- Product catalog management
- GraphQL queries and mutations
- Product inventory tracking
- Product search and filtering

**APIs (3 endpoints):**
- `POST /api/products` - Create product
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products` - List all products

**GraphQL Endpoint:**
- `POST /graphql` - GraphQL queries and mutations

**Kafka Events:**
- `PRODUCT_CREATED` - When new product is created
- `PRODUCT_UPDATED` - When product is updated
- `PRODUCT_DELETED` - When product is deleted

### Order Service (Port: 8083)
**Responsibilities:**
- Order processing and management
- Order status tracking
- Order history
- Integration with User and Product services

**APIs (3 endpoints):**
- `POST /api/orders` - Create order
- `GET /api/orders/{id}` - Get order by ID
- `PUT /api/orders/{id}/status` - Update order status

**Kafka Events:**
- `ORDER_CREATED` - When new order is created
- `ORDER_UPDATED` - When order is updated
- `ORDER_COMPLETED` - When order is completed

### Eureka Server (Port: 8761)
**Responsibilities:**
- Service registration and discovery
- Health monitoring
- Service dashboard
- Load balancing support

**Features:**
- Service registration
- Health checks
- Service discovery
- Dashboard for monitoring

### API Gateway (Port: 8080)
**Responsibilities:**
- Route requests to appropriate services
- Load balancing
- JWT authentication
- Rate limiting
- CORS configuration

**Routes:**
- `/api/auth/**` → User Service
- `/api/users/**` → User Service
- `/api/products/**` → Product Service
- `/api/orders/**` → Order Service
- `/graphql` → Product Service

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

### Development
- Build Tool: Maven
- Testing: JUnit 5, Mockito
- API Testing: Swagger UI

## Project Structure

```
MicroservicesBasics/
├── user-service/           # User management (existing)
├── product-service/        # Product catalog with GraphQL
├── order-service/         # Order processing
├── eureka-server/         # Service discovery
├── api-gateway/          # API Gateway with load balancing
├── kafka-setup/          # Kafka configuration
└── docs/                 # Project documentation
```

## Security Architecture

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

## Deployment Strategy

### Development Environment
- Local Development: All services run locally
- Docker Compose: For Kafka and other dependencies
- In-Memory Databases: H2 for development

### Production Considerations
- Containerization: Docker containers for each service
- Orchestration: Kubernetes for production deployment
- Database: PostgreSQL or MySQL for production
- Monitoring: Prometheus and Grafana

## Testing Strategy

### Unit Testing
- Service layer business logic
- Controller API endpoints
- Repository data access
- Kafka producer/consumer

### Integration Testing
- Service communication via Kafka
- API Gateway routing
- GraphQL queries and mutations
- Eureka service registration

### End-to-End Testing
- Complete user journey
- Order creation flow
- Product catalog browsing
- Health check verification

## Learning Objectives

### Microservices Concepts
- Service decomposition
- Service discovery
- API gateway patterns
- Event-driven architecture

### Spring Cloud
- Eureka service discovery
- Spring Cloud Gateway
- Spring Cloud Stream (Kafka)

### GraphQL
- Schema definition
- Resolvers
- Queries and mutations

### Event-Driven Architecture
- Kafka producer/consumer
- Event sourcing
- Saga pattern

### Monitoring & Observability
- Health checks
- Metrics collection
- Distributed tracing

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

---

Next Steps: Follow the implementation phases in order, starting with infrastructure setup and building up to a complete microservices ecosystem. 