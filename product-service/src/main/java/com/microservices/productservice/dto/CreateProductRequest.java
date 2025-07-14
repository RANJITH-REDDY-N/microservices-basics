package com.microservices.productservice.dto;

import com.microservices.productservice.entity.ProductCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateProductRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
    @NotNull
    private ProductCategory category;
    @NotNull
    @Min(0)
    private Integer stockQuantity;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
} 