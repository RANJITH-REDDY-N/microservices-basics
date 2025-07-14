package com.microservices.productservice.service;

import com.microservices.productservice.dto.CreateProductRequest;
import com.microservices.productservice.dto.ProductDto;
import com.microservices.productservice.dto.UpdateProductRequest;
import com.microservices.productservice.entity.Product;
import com.microservices.productservice.entity.ProductCategory;
import com.microservices.productservice.exception.ProductNotFoundException;
import com.microservices.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDto createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product saved = productRepository.save(product);
        // TODO: Publish Kafka event here (stub)
        return toDto(saved);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toDto(product);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ProductDto> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ProductDto> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        product.setUpdatedAt(LocalDateTime.now());
        Product saved = productRepository.save(product);
        // TODO: Publish Kafka event here (stub)
        return toDto(saved);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
        // TODO: Publish Kafka event here (stub)
    }

    public ProductDto updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setStockQuantity(quantity);
        product.setUpdatedAt(LocalDateTime.now());
        Product saved = productRepository.save(product);
        // TODO: Publish Kafka event here (stub)
        return toDto(saved);
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
} 