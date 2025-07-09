package com.microservices.userservice.service;

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

    public void publishUserCreated(String username, Long userId) {
        try {
            String message = objectMapper.writeValueAsString(new UserEvent("USER_CREATED", userId, username));
            kafkaTemplate.send("user-events", message);
        } catch (JsonProcessingException e) {
            // Log error but don't throw exception to avoid breaking the main flow
            System.err.println("Error publishing user created event: " + e.getMessage());
        }
    }

    public void publishUserUpdated(String username, Long userId) {
        try {
            String message = objectMapper.writeValueAsString(new UserEvent("USER_UPDATED", userId, username));
            kafkaTemplate.send("user-events", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error publishing user updated event: " + e.getMessage());
        }
    }

    public void publishUserDeleted(String username, Long userId) {
        try {
            String message = objectMapper.writeValueAsString(new UserEvent("USER_DELETED", userId, username));
            kafkaTemplate.send("user-events", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error publishing user deleted event: " + e.getMessage());
        }
    }

    // User Event class
    public static class UserEvent {
        private String eventType;
        private Long userId;
        private String username;
        private long timestamp;

        public UserEvent(String eventType, Long userId, String username) {
            this.eventType = eventType;
            this.userId = userId;
            this.username = username;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and Setters
        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
} 