package com.microservices.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.productservice.controller.ProductController;
import com.microservices.productservice.dto.CreateProductRequest;
import com.microservices.productservice.dto.ProductDto;
import com.microservices.productservice.entity.ProductCategory;
import com.microservices.productservice.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Should create product successfully")
    void testCreateProduct() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Test Product");
        request.setDescription("A test product");
        request.setPrice(BigDecimal.valueOf(10.0));
        request.setCategory(ProductCategory.ELECTRONICS);
        request.setStockQuantity(5);

        ProductDto response = new ProductDto();
        response.setId(1L);
        response.setName("Test Product");
        response.setDescription("A test product");
        response.setPrice(BigDecimal.valueOf(10.0));
        response.setCategory(ProductCategory.ELECTRONICS);
        response.setStockQuantity(5);

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.category").value("ELECTRONICS"));
    }
} 