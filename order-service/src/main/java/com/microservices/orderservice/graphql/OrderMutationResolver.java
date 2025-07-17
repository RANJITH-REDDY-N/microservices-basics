package com.microservices.orderservice.graphql;

import com.microservices.orderservice.dto.OrderDto;
import com.microservices.orderservice.entity.OrderStatus;
import com.microservices.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class OrderMutationResolver {
    private final OrderService orderService;

    @Autowired
    public OrderMutationResolver(OrderService orderService) {
        this.orderService = orderService;
    }

    @MutationMapping
    public OrderDto updateOrderStatus(@Argument Long id, @Argument OrderStatus status, @Argument Long userId, @Argument String role) {
        return orderService.updateOrderStatus(id, status, userId, role);
    }
} 