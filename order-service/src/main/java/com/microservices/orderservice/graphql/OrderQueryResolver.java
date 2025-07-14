package com.microservices.orderservice.graphql;

import com.microservices.orderservice.dto.OrderDto;
import com.microservices.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class OrderQueryResolver {
    private final OrderService orderService;

    @Autowired
    public OrderQueryResolver(OrderService orderService) {
        this.orderService = orderService;
    }

    @QueryMapping
    public OrderDto order(@Argument Long id) {
        return orderService.getOrderById(id);
    }
} 