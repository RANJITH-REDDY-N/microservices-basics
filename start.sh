#!/bin/bash

# Remove existing Kafka/Zookeeper containers if they exist
for cname in kafka zookeeper kafka-ui; do
  if [ "$(docker ps -aq -f name=^/${cname}$)" ]; then
    echo "Removing existing container: $cname"
    docker rm -f $cname
  fi
done

# Kill processes on all relevant ports before starting
for port in 8080 8081 8082 8083 8761 9092; do
  PID=$(lsof -ti tcp:$port)
  if [ -n "$PID" ]; then
    echo "Killing process on port $port (PID $PID)..."
    kill -9 $PID
  fi
  # Show port usage after kill
  echo "Checking port $port after kill:"
  lsof -i :$port
  echo "---"
done
sleep 2  # Wait to ensure ports are released

# 1. Start Kafka and Zookeeper
echo "Starting Kafka and Zookeeper..."
cd kafka-setup

docker-compose up -d

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
sleep 30

# Ensure create-topics.sh is executable
chmod +x create-topics.sh

# 2. Create Kafka topics
echo "Creating Kafka topics..."
./create-topics.sh

cd ..

# 3. Start Eureka Server
echo "Building and starting Eureka Server (skipping tests)..."
cd eureka-server
mvn clean install -DskipTests
mvn spring-boot:run &
cd ..

# 4. Start User Service
echo "Building and starting User Service (skipping tests)..."
cd user-service
mvn clean install -DskipTests
mvn spring-boot:run &
cd ..

# 5. Start Product Service
echo "Building and starting Product Service (skipping tests)..."
cd product-service
mvn clean install -DskipTests
mvn spring-boot:run &
cd ..

# 6. Start Order Service
echo "Building and starting Order Service (skipping tests)..."
cd order-service
mvn clean install -DskipTests
mvn spring-boot:run &
cd ..

# 7. Start API Gateway LAST
echo "Building and starting API Gateway (skipping tests)..."
cd api-gateway
mvn clean install -DskipTests
mvn spring-boot:run &
cd ..

echo "All services are starting up in the background (tests skipped)!" 