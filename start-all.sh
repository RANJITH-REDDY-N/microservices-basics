#!/bin/bash

# 1. Start Kafka and Zookeeper
echo "Starting Kafka and Zookeeper..."
cd kafka-setup
docker-compose up -d

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
sleep 30

# 2. Create Kafka topics
echo "Creating Kafka topics..."
./create-topics.sh

cd ..

# 3. Start Eureka Server
echo "Running tests for Eureka Server..."
cd eureka-server
if mvn clean test; then
  echo "Tests passed. Building and starting Eureka Server..."
  mvn clean install
  osascript -e 'tell app "Terminal" to do script "cd \"$(pwd)\" && mvn spring-boot:run"'
else
  echo "Tests failed for Eureka Server. Not starting."
fi
cd ..

# 4. Start User Service
echo "Running tests for User Service..."
cd user-service
if mvn clean test; then
  echo "Tests passed. Building and starting User Service..."
  mvn clean install
  osascript -e 'tell app "Terminal" to do script "cd \"$(pwd)\" && mvn spring-boot:run"'
else
  echo "Tests failed for User Service. Not starting."
fi
cd ..

# 5. Start Product Service
echo "Running tests for Product Service..."
cd product-service
if mvn clean test; then
  echo "Tests passed. Building and starting Product Service..."
  mvn clean install
  osascript -e 'tell app "Terminal" to do script "cd \"$(pwd)\" && mvn spring-boot:run"'
else
  echo "Tests failed for Product Service. Not starting."
fi
cd ..

# 6. Start Order Service
echo "Running tests for Order Service..."
cd order-service
if mvn clean test; then
  echo "Tests passed. Building and starting Order Service..."
  mvn clean install
  osascript -e 'tell app "Terminal" to do script "cd \"$(pwd)\" && mvn spring-boot:run"'
else
  echo "Tests failed for Order Service. Not starting."
fi
cd ..

# 7. Start API Gateway
echo "Running tests for API Gateway..."
cd api-gateway
if mvn clean test; then
  echo "Tests passed. Building and starting API Gateway..."
  mvn clean install
  osascript -e 'tell app "Terminal" to do script "cd \"$(pwd)\" && mvn spring-boot:run"'
else
  echo "Tests failed for API Gateway. Not starting."
fi
cd ..

echo "All services that passed tests are starting up in new Terminal windows/tabs!"