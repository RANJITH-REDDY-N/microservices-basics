package com.microservices.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishProductCreated(Long productId, String productName, String category) {
        try {
            String message = objectMapper.writeValueAsString(new ProductEvent("PRODUCT_CREATED", productId, productName, category));
            kafkaTemplate.send("product-events", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error publishing product created event: " + e.getMessage());
        }
    }

    public void publishProductUpdated(Long productId, String productName, String category) {
        try {
            String message = objectMapper.writeValueAsString(new ProductEvent("PRODUCT_UPDATED", productId, productName, category));
            kafkaTemplate.send("product-events", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error publishing product updated event: " + e.getMessage());
        }
    }

    public void publishProductDeleted(Long productId, String productName, String category) {
        try {
            String message = objectMapper.writeValueAsString(new ProductEvent("PRODUCT_DELETED", productId, productName, category));
            kafkaTemplate.send("product-events", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error publishing product deleted event: " + e.getMessage());
        }
    }

    // Product Event class
    public static class ProductEvent {
        private String eventType;
        private Long productId;
        private String productName;
        private String category;
        private long timestamp;

        public ProductEvent(String eventType, Long productId, String productName, String category) {
            this.eventType = eventType;
            this.productId = productId;
            this.productName = productName;
            this.category = category;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and Setters
        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
} 