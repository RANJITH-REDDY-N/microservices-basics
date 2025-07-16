#!/bin/bash

# Kafka Topic Creation Script
# This script creates the required topics for the microservices project

echo "Creating Kafka topics..."

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
sleep 30

# Create user-events topic
echo "Creating user-events topic..."
docker exec -it kafka kafka-topics --create \
    --topic user-events \
    --bootstrap-server localhost:9092 \
    --partitions 3 \
    --replication-factor 1 \
    --if-not-exists

# Create product-events topic
echo "Creating product-events topic..."
docker exec -it kafka kafka-topics --create \
    --topic product-events \
    --bootstrap-server localhost:9092 \
    --partitions 3 \
    --replication-factor 1 \
    --if-not-exists

# Create order-events topic
echo "Creating order-events topic..."
docker exec -it kafka kafka-topics --create \
    --topic order-events \
    --bootstrap-server localhost:9092 \
    --partitions 3 \
    --replication-factor 1 \
    --if-not-exists

# List all topics
echo "Listing all topics..."
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

echo "Topic creation completed!" 