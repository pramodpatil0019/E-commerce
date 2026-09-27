package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.exception.ResourceNotFoundException;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PLACED);

        List<OrderItem> items = request.getItems().stream().map(itemDto -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemDto.getProductId());
            item.setProductName(itemDto.getProductName());
            item.setPrice(itemDto.getPrice());
            item.setQuantity(itemDto.getQuantity());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);
        order.setTotalAmount(calculateTotal(items));

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Async
    public void processPaymentAsync(Order order) {
        try {
            String paymentUrl = "http://payment-service/api/payments/process";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> paymentRequest = new HashMap<>();
            paymentRequest.put("orderId", order.getId());
            paymentRequest.put("userId", order.getUserId());
            paymentRequest.put("amount", order.getTotalAmount());
            paymentRequest.put("paymentMethod", order.getPaymentMethod());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentRequest, headers);
            restTemplate.exchange(paymentUrl, HttpMethod.POST, request, Object.class);
        } catch (Exception e) {
            // Handle error, maybe update order status to FAILED
            System.err.println("Error processing payment: " + e.getMessage());
        }
    }

    public List<OrderResponse> getOrderHistory(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        return mapToResponse(order);
    }

    public OrderResponse trackOrder(Long userId, Long orderId) {
        return getOrderById(userId, orderId);
    }

    @Transactional
    public OrderResponse updateOrderStatus(OrderStatusUpdate update) {
        Order order = orderRepository.findById(update.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + update.getOrderId()));
        
        try {
            order.setStatus(OrderStatus.valueOf(update.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Invalid status handled or ignored
        }
        
        if (update.getPaymentId() != null) {
            order.setPaymentId(update.getPaymentId());
        }
        
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> OrderItemDto.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build()).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .items(itemDtos)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentId(order.getPaymentId())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
