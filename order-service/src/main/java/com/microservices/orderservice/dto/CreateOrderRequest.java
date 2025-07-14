package com.microservices.orderservice.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class CreateOrderRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String username;
    @NotNull
    @Size(min = 1)
    private List<OrderItemRequest> orderItems;

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<OrderItemRequest> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemRequest> orderItems) { this.orderItems = orderItems; }

    public static class OrderItemRequest {
        @NotNull
        private Long productId;
        @NotBlank
        private String productName;
        @NotNull
        @Min(1)
        private Integer quantity;
        @NotNull
        @DecimalMin("0.0")
        private java.math.BigDecimal unitPrice;

        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public java.math.BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }
} 