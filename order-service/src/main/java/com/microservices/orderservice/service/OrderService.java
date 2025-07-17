package com.microservices.orderservice.service;

import com.microservices.orderservice.dto.CreateOrderRequest;
import com.microservices.orderservice.dto.OrderDto;
import com.microservices.orderservice.dto.OrderItemDto;
import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.entity.OrderItem;
import com.microservices.orderservice.entity.OrderStatus;
import com.microservices.orderservice.exception.OrderNotFoundException;
import com.microservices.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaProducerService kafkaProducerService;
    private final RestTemplate restTemplate;
    @Value("${product.service.url:http://api-gateway:8080/api/products}")
    private String productServiceUrl;

    @Autowired
    public OrderService(OrderRepository orderRepository, KafkaProducerService kafkaProducerService, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setUsername(request.getUsername());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        List<OrderItem> items = request.getOrderItems().stream().map(itemReq -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemReq.getProductId());
            item.setProductName(itemReq.getProductName());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setTotalPrice(itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            return item;
        }).collect(Collectors.toList());
        order.setOrderItems(items);
        order.setTotalAmount(items.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));

        Order saved = orderRepository.save(order);
        // Publish order created event
        kafkaProducerService.publishOrderCreated(saved.getId(), saved.getUserId(), saved.getTotalAmount());
        return toDto(saved);
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toDto(order);
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrderStatus(Long id, OrderStatus status, Long requestUserId, String requestUserRole) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        boolean isOwner = order.getUserId().equals(requestUserId);
        boolean isAdminOrManager = "ADMIN".equals(requestUserRole) || "MODERATOR".equals(requestUserRole);

        // Ownership/role enforcement
        if (status == OrderStatus.CANCELLED) {
            if (!(isOwner || isAdminOrManager)) {
                throw new RuntimeException("Only the order owner or admin/manager can cancel the order.");
            }
        } else if (status == OrderStatus.CONFIRMED) {
            if (!isAdminOrManager) {
                throw new RuntimeException("Only admin/manager can confirm orders.");
            }
            // Stock check logic
            for (OrderItem item : order.getOrderItems()) {
                String url = productServiceUrl + "/" + item.getProductId();
                ProductStockResponse product = restTemplate.getForObject(url, ProductStockResponse.class);
                if (product == null || product.getStockQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + item.getProductName());
                }
            }
            // Optionally, call Product Service to decrement stock here or rely on event
        } else if (status == OrderStatus.DELIVERED || status == OrderStatus.SHIPPED) {
            if (!isAdminOrManager) {
                throw new RuntimeException("Only admin/manager can update to delivered/shipped.");
            }
        }
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        kafkaProducerService.publishOrderUpdated(saved.getId(), saved.getUserId(), saved.getStatus().name());
        if (status == OrderStatus.DELIVERED) {
            kafkaProducerService.publishOrderCompleted(saved.getId(), saved.getUserId(), saved.getTotalAmount());
        }
        return toDto(saved);
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setUsername(order.getUsername());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setOrderItems(order.getOrderItems().stream().map(this::toItemDto).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDto toItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    // Helper class for product stock response
    private static class ProductStockResponse {
        private Long id;
        private String name;
        private int stockQuantity;
        public int getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
        // getters/setters for id, name
    }
} 