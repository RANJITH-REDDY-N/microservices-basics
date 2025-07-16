#!/bin/bash

# Kill API Gateway (port 8080) first
PID=$(lsof -ti tcp:8080)
if [ -n "$PID" ]; then
  echo "Killing API Gateway process on port 8080 (PID $PID)..."
  kill -9 $PID
  sleep 2
fi

# Stop and remove Kafka, Zookeeper, and Kafka UI containers
cd kafka-setup 2>/dev/null || true
if [ -f docker-compose.yml ]; then
  echo "Stopping Kafka/Zookeeper/Kafka UI containers..."
  docker-compose down
  cd ..
else
  cd ..
fi

# Kill all other Spring Boot services started by Maven (spring-boot:run)
echo "Killing all Spring Boot services started by Maven (except API Gateway)..."
for port in 8081 8082 8083 8761 9092; do
  PID=$(lsof -ti tcp:$port)
  if [ -n "$PID" ]; then
    echo "Killing process on port $port (PID $PID)..."
    kill -9 $PID
  fi
done

# Kill any remaining process on relevant ports
echo "Killing any remaining process on ports 8080, 8081, 8082, 8083, 8761, 9092..."
for port in 8080 8081 8082 8083 8761 9092; do
  PID=$(lsof -ti tcp:$port)
  if [ -n "$PID" ]; then
    echo "Killing process on port $port (PID $PID)..."
    kill -9 $PID
  fi
done

sleep 2

echo "All microservices and infrastructure have been stopped." 