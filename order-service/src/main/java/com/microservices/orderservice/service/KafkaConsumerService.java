package com.microservices.orderservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "user-events", groupId = "order-service-group")
    public void handleUserEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            Long userId = event.get("userId").asLong();
            String username = event.get("username").asText();

            logger.info("Received user event: {} for user: {} ({})", eventType, username, userId);

            switch (eventType) {
                case "USER_CREATED":
                    handleUserCreated(userId, username);
                    break;
                case "USER_UPDATED":
                    handleUserUpdated(userId, username);
                    break;
                case "USER_DELETED":
                    handleUserDeleted(userId, username);
                    break;
                default:
                    logger.warn("Unknown user event type: {}", eventType);
            }
        } catch (Exception e) {
            logger.error("Error processing user event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "product-events", groupId = "order-service-group")
    public void handleProductEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            Long productId = event.get("productId").asLong();
            String productName = event.get("productName").asText();
            String category = event.get("category").asText();

            logger.info("Received product event: {} for product: {} ({})", eventType, productName, productId);

            switch (eventType) {
                case "PRODUCT_CREATED":
                    handleProductCreated(productId, productName, category);
                    break;
                case "PRODUCT_UPDATED":
                    handleProductUpdated(productId, productName, category);
                    break;
                case "PRODUCT_DELETED":
                    handleProductDeleted(productId, productName, category);
                    break;
                default:
                    logger.warn("Unknown product event type: {}", eventType);
            }
        } catch (Exception e) {
            logger.error("Error processing product event: {}", e.getMessage(), e);
        }
    }

    private void handleUserCreated(Long userId, String username) {
        // Handle user created event
        // Could update local cache, validate orders, etc.
        logger.info("User created: {} ({})", username, userId);
    }

    private void handleUserUpdated(Long userId, String username) {
        // Handle user updated event
        // Could update order information, etc.
        logger.info("User updated: {} ({})", username, userId);
    }

    private void handleUserDeleted(Long userId, String username) {
        // Handle user deleted event
        // Could cancel pending orders, etc.
        logger.info("User deleted: {} ({})", username, userId);
    }

    private void handleProductCreated(Long productId, String productName, String category) {
        // Handle product created event
        // Could update product catalog, etc.
        logger.info("Product created: {} ({}) in category: {}", productName, productId, category);
    }

    private void handleProductUpdated(Long productId, String productName, String category) {
        // Handle product updated event
        // Could update order items, etc.
        logger.info("Product updated: {} ({}) in category: {}", productName, productId, category);
    }

    private void handleProductDeleted(Long productId, String productName, String category) {
        // Handle product deleted event
        // Could mark orders as unavailable, etc.
        logger.info("Product deleted: {} ({}) in category: {}", productName, productId, category);
    }
} 