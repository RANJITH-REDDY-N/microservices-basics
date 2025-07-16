package com.microservices.productservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.productservice.entity.Product;
import com.microservices.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @KafkaListener(topics = "order-events", groupId = "product-service-group")
    @Transactional
    public void handleOrderEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            Long orderId = event.get("orderId").asLong();
            logger.info("Received order event: {} for order: {}", eventType, orderId);

            if ("ORDER_COMPLETED".equals(eventType)) {
                JsonNode orderItems = event.get("orderItems");
                if (orderItems != null && orderItems.isArray()) {
                    for (JsonNode item : orderItems) {
                        Long productId = item.get("productId").asLong();
                        int quantity = item.get("quantity").asInt();
                        productRepository.findById(productId).ifPresent(product -> {
                            int newStock = product.getStockQuantity() - quantity;
                            product.setStockQuantity(Math.max(newStock, 0));
                            productRepository.save(product);
                            logger.info("Decremented stock for product {} by {}. New stock: {}", productId, quantity, product.getStockQuantity());
                        });
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error processing order event: {}", e.getMessage(), e);
        }
    }
} 