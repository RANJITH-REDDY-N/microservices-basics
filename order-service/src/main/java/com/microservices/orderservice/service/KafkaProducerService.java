package com.microservices.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishOrderCreated(Long orderId, Long userId, BigDecimal totalAmount) {
        try {
            String message = objectMapper.writeValueAsString(new OrderEvent("ORDER_CREATED", orderId, userId, totalAmount));
            kafkaTemplate.send("order-events", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error publishing order created event: " + e.getMessage());
        }
    }

    public void publishOrderUpdated(Long orderId, Long userId, String status) {
        try {
            String message = objectMapper.writeValueAsString(new OrderEvent("ORDER_UPDATED", orderId, userId, status));
            kafkaTemplate.send("order-events", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error publishing order updated event: " + e.getMessage());
        }
    }

    public void publishOrderCompleted(Long orderId, Long userId, BigDecimal totalAmount) {
        try {
            String message = objectMapper.writeValueAsString(new OrderEvent("ORDER_COMPLETED", orderId, userId, totalAmount));
            kafkaTemplate.send("order-events", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error publishing order completed event: " + e.getMessage());
        }
    }

    // Order Event class
    public static class OrderEvent {
        private String eventType;
        private Long orderId;
        private Long userId;
        private Object data; // Can be totalAmount (BigDecimal) or status (String)
        private long timestamp;

        public OrderEvent(String eventType, Long orderId, Long userId, Object data) {
            this.eventType = eventType;
            this.orderId = orderId;
            this.userId = userId;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and Setters
        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
} 