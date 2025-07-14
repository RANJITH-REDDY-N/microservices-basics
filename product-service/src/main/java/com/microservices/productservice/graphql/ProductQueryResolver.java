package com.microservices.productservice.graphql;

import com.microservices.productservice.dto.ProductDto;
import com.microservices.productservice.entity.ProductCategory;
import com.microservices.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductQueryResolver {
    private final ProductService productService;

    @Autowired
    public ProductQueryResolver(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public List<ProductDto> products() {
        return productService.getAllProducts();
    }

    @QueryMapping
    public ProductDto product(@Argument Long id) {
        return productService.getProductById(id);
    }

    @QueryMapping
    public List<ProductDto> productsByCategory(@Argument ProductCategory category) {
        return productService.getProductsByCategory(category);
    }

    @QueryMapping
    public List<ProductDto> searchProducts(@Argument String name) {
        return productService.searchProductsByName(name);
    }
} 