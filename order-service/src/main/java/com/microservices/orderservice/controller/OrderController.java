package com.microservices.orderservice.controller;

import com.microservices.orderservice.dto.CreateOrderRequest;
import com.microservices.orderservice.dto.OrderDto;
import com.microservices.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservices.orderservice.dto.UpdateOrderStatusRequest;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = orderService.createOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@RequestParam(required = false) Long userId, @RequestParam(required = false) String role) {
        if (role != null && (role.equals("ADMIN") || role.equals("MODERATOR"))) {
            return ResponseEntity.ok(orderService.getAllOrders());
        } else if (userId != null) {
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request,
                                                     @RequestParam Long userId, @RequestParam String role) {
        OrderDto order = orderService.updateOrderStatus(id, request.getStatus(), userId, role);
        return ResponseEntity.ok(order);
    }
} 