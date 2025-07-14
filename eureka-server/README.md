# Eureka Server - Service Discovery

## Overview

Eureka Server provides service discovery and registration capabilities for the microservices architecture. It acts as a central registry where all services register themselves and discover other services.

## Architecture Role

- Service Registration: All microservices register with Eureka
- Service Discovery: Services can discover each other through Eureka
- Health Monitoring: Automatic health checks for registered services
- Load Balancing: Supports client-side load balancing

## Features

### Core Functionality
- Service Registration: Services register their instances
- Service Discovery: Services can find other services
- Health Monitoring: Automatic health checks
- Dashboard: Web UI for monitoring registered services

### Configuration
- Port: 8761
- Dashboard: http://localhost:8761
- Self-Preservation: Enabled by default
- Eviction: Automatic cleanup of unhealthy instances

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Running the Service
```bash
# Navigate to eureka-server directory
cd eureka-server

# Build the project
mvn clean install

# Run the service
mvn spring-boot:run
```

### Verification
- Dashboard: http://localhost:8761
- Health Check: http://localhost:8761/actuator/health

## Dashboard Features

### Service Registry
- View all registered services
- Service instance details
- Health status of each service
- Instance count per service

### Monitoring
- Real-time service status
- Instance information
- Service metadata
- Health indicators

## Configuration

### Application Properties
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

### Key Properties
- `register-with-eureka: false` - Server doesn't register itself
- `fetch-registry: false` - Server doesn't fetch registry
- `enable-self-preservation: true` - Prevents service eviction
- `eviction-interval-timer-in-ms: 1000` - Health check interval

## Service Registration Flow

### Registration Process
1. Service Startup: Service starts and reads configuration
2. Eureka Client: Service creates Eureka client
3. Registration Request: Service sends registration to Eureka
4. Heartbeat: Service sends periodic heartbeats
5. Health Check: Eureka monitors service health

### Discovery Process
1. Service Request: Service needs to call another service
2. Eureka Query: Service queries Eureka for target service
3. Instance Selection: Eureka returns available instances
4. Load Balancing: Client-side load balancing if multiple instances
5. Service Call: Service makes HTTP call to selected instance

## Dependencies

### Core Dependencies
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

### Spring Cloud Version
```xml
<properties>
    <spring-cloud.version>2023.0.0</spring-cloud.version>
</properties>
```

## Monitoring & Health

### Health Endpoints
- `/actuator/health` - Service health status
- `/actuator/info` - Service information
- `/actuator/metrics` - Service metrics

### Dashboard Monitoring
- Service Count: Number of registered services
- Instance Count: Total number of instances
- Health Status: Overall system health
- Memory Usage: Server resource usage

## Security Considerations

### Development Environment
- No authentication required
- Dashboard accessible to all
- Self-preservation enabled

### Production Environment
- Enable authentication
- Secure dashboard access
- Configure proper network security
- Use HTTPS for communication

## Testing

### Manual Testing
1. Start Eureka Server
2. Access dashboard at http://localhost:8761
3. Verify server is running
4. Check health endpoints

### Integration Testing
1. Start Eureka Server
2. Start other services
3. Verify services register with Eureka
4. Check service discovery works

## Learning Objectives

### Service Discovery Concepts
- Registration: How services register themselves
- Discovery: How services find each other
- Health Monitoring: How health is tracked
- Load Balancing: How load is distributed

### Eureka Specific
- Client-Side Load Balancing: Using Eureka for load balancing
- Service Registry: Understanding the registry pattern
- Health Checks: How Eureka monitors service health
- Self-Preservation: Preventing service eviction

## Production Considerations

### High Availability
- Deploy multiple Eureka instances
- Configure peer-to-peer communication
- Implement proper backup strategies

### Security
- Implement authentication and authorization
- Secure dashboard access
- Configure proper network security

### Monitoring
- Set up alerts and dashboards
- Monitor service registration patterns
- Track health check failures

---

**Port**: 8761  
**Dashboard**: http://localhost:8761