package com.microservices.productservice.graphql;

import com.microservices.productservice.dto.CreateProductRequest;
import com.microservices.productservice.dto.ProductDto;
import com.microservices.productservice.dto.UpdateProductRequest;
import com.microservices.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProductMutationResolver {
    private final ProductService productService;

    @Autowired
    public ProductMutationResolver(ProductService productService) {
        this.productService = productService;
    }

    @MutationMapping
    public ProductDto createProduct(@Argument CreateProductRequest input) {
        return productService.createProduct(input);
    }

    @MutationMapping
    public ProductDto updateProduct(@Argument Long id, @Argument UpdateProductRequest input) {
        return productService.updateProduct(id, input);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        productService.deleteProduct(id);
        return true;
    }

    @MutationMapping
    public ProductDto updateStock(@Argument Long id, @Argument Integer quantity) {
        return productService.updateStock(id, quantity);
    }
} 