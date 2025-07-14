# Kafka Setup - Message Broker

## Overview

Kafka setup provides the message broker infrastructure for asynchronous communication between microservices. It enables event-driven architecture and decouples services through event publishing and consumption.

## Architecture Role

- Message Broker: Centralized message handling
- Event Streaming: Real-time event processing
- Service Decoupling: Loose coupling between services
- Event Storage: Persistent event storage
- Scalability: Horizontal scaling capabilities

## Features

### Core Functionality
- Event Publishing: Services publish events to topics
- Event Consumption: Services consume events from topics
- Topic Management: Create and manage Kafka topics
- Partitioning: Distribute load across partitions
- Replication: Fault tolerance through replication

### Topics Configuration
- `user-events` - User service events
- `product-events` - Product service events
- `order-events` - Order service events

## Quick Start

### Prerequisites
- Docker and Docker Compose
- At least 4GB available memory

### Running Kafka
```bash
# Navigate to kafka-setup directory
cd kafka-setup

# Start Kafka and Zookeeper
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### Verification
- Kafka: localhost:9092
- Zookeeper: localhost:2181
- Kafka UI: http://localhost:8080 (if configured)

## Configuration

### Docker Compose
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
    networks:
      - kafka-network

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
    networks:
      - kafka-network

  # Optional: Kafka UI for monitoring
  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    depends_on:
      - kafka
    ports:
      - "8080:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
      KAFKA_CLUSTERS_0_ZOOKEEPER: zookeeper:2181
    networks:
      - kafka-network

volumes:
  zookeeper-data:
  zookeeper-logs:
  kafka-data:

networks:
  kafka-network:
    driver: bridge
```

### Topic Configuration
```bash
# Create topics
docker exec -it kafka kafka-topics --create --topic user-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

docker exec -it kafka kafka-topics --create --topic product-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

docker exec -it kafka kafka-topics --create --topic order-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# List topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Describe topics
docker exec -it kafka kafka-topics --describe --topic user-events --bootstrap-server localhost:9092
```

## Event Schemas

### User Events
```json
{
  "eventType": "USER_CREATED",
  "userId": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Product Events
```json
{
  "eventType": "PRODUCT_CREATED",
  "productId": 1,
  "productName": "MacBook Pro",
  "category": "ELECTRONICS",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Order Events
```json
{
  "eventType": "ORDER_CREATED",
  "orderId": 1,
  "userId": 1,
  "totalAmount": 1299.99,
  "timestamp": "2024-01-01T10:00:00Z"
}
```

## Event Flow

### User Service Events
```
User Service → user-events → Order Service
```

**Events Published:**
- `USER_CREATED` - When new user is registered
- `USER_UPDATED` - When user profile is updated
- `USER_DELETED` - When user is deleted

### Product Service Events
```
Product Service → product-events → Order Service
```

**Events Published:**
- `PRODUCT_CREATED` - When new product is created
- `PRODUCT_UPDATED` - When product is updated
- `PRODUCT_DELETED` - When product is deleted

### Order Service Events
```
Order Service → order-events → User Service
```

**Events Published:**
- `ORDER_CREATED` - When new order is created
- `ORDER_UPDATED` - When order is updated
- `ORDER_COMPLETED` - When order is completed

## Kafka Commands

### Topic Management
```bash
# Create topic
kafka-topics --create --topic topic-name --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# List topics
kafka-topics --list --bootstrap-server localhost:9092

# Describe topic
kafka-topics --describe --topic topic-name --bootstrap-server localhost:9092

# Delete topic
kafka-topics --delete --topic topic-name --bootstrap-server localhost:9092
```

### Producer/Consumer Testing
```bash
# Start producer
kafka-console-producer --topic user-events --bootstrap-server localhost:9092

# Start consumer
kafka-console-consumer --topic user-events --bootstrap-server localhost:9092 --from-beginning
```

### Consumer Group Management
```bash
# List consumer groups
kafka-consumer-groups --list --bootstrap-server localhost:9092

# Describe consumer group
kafka-consumer-groups --describe --group group-name --bootstrap-server localhost:9092

# Reset consumer group offset
kafka-consumer-groups --bootstrap-server localhost:9092 --group group-name --topic topic-name --reset-offsets --to-earliest --execute
```

## Monitoring

### Kafka Metrics
- Broker Metrics: CPU, memory, disk usage
- Topic Metrics: Messages per second, bytes per second
- Consumer Metrics: Consumer lag, offset commits
- Producer Metrics: Message rate, batch size

### Health Checks
```bash
# Check if Kafka is running
docker exec -it kafka kafka-broker-api-versions --bootstrap-server localhost:9092

# Check topic health
docker exec -it kafka kafka-topics --describe --bootstrap-server localhost:9092
```

## Testing

### Manual Testing
1. Start Kafka using Docker Compose
2. Create topics for each service
3. Test producer/consumer manually
4. Verify event flow between services

### Integration Testing
1. Test event publishing from services
2. Test event consumption by services
3. Test error handling and retry logic
4. Test consumer group behavior

### Performance Testing
1. Test message throughput
2. Test consumer lag
3. Test partition distribution
4. Test replication factor

## Learning Objectives

### Kafka Concepts
- Topics: Understanding Kafka topics and partitions
- Producers: How to publish messages to topics
- Consumers: How to consume messages from topics
- Consumer Groups: Managing consumer groups and offsets

### Event-Driven Architecture
- Event Publishing: When and how to publish events
- Event Consumption: How to consume and process events
- Event Schema: Designing event schemas
- Event Flow: Understanding event flow between services

### Spring Kafka
- KafkaTemplate: Using KafkaTemplate for publishing
- @KafkaListener: Using annotations for consuming
- Error Handling: Handling Kafka errors and retries
- Configuration: Configuring Kafka producers and consumers

## Troubleshooting

### Common Issues
1. Connection Refused: Check if Kafka is running
2. Topic Not Found: Create topics before use
3. Consumer Lag: Monitor consumer performance
4. Memory Issues: Increase Docker memory allocation

### Debug Commands
```bash
# Check Kafka logs
docker-compose logs kafka

# Check Zookeeper logs
docker-compose logs zookeeper

# Check topic details
docker exec -it kafka kafka-topics --describe --bootstrap-server localhost:9092

# Check consumer groups
docker exec -it kafka kafka-consumer-groups --list --bootstrap-server localhost:9092
```

## Production Considerations

### High Availability
- Deploy multiple Kafka brokers
- Configure proper replication
- Implement monitoring and alerting

### Security
- Enable SSL/TLS for all communications
- Implement authentication and authorization
- Configure proper network security

### Monitoring
- Set up comprehensive monitoring
- Monitor broker health and performance
- Track consumer lag and throughput

### Backup
- Implement topic backup strategies
- Configure data retention policies
- Set up disaster recovery procedures

---

**Port**: 9092 (Kafka), 2181 (Zookeeper)  
**Docker Compose**: docker-compose up -d