package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.ApiResponse;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.dto.OrderStatusUpdate;
import com.ecommerce.orderservice.dto.PlaceOrderRequest;
import com.ecommerce.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@RequestHeader("X-User-Id") String userId,
                                                                 @RequestBody @Valid PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(Long.valueOf(userId), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderHistory(@RequestHeader("X-User-Id") String userId) {
        List<OrderResponse> response = orderService.getOrderHistory(Long.valueOf(userId));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@RequestHeader("X-User-Id") String userId,
                                                                   @PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(Long.valueOf(userId), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/track")
    public ResponseEntity<ApiResponse<OrderResponse>> trackOrder(@RequestHeader("X-User-Id") String userId,
                                                                 @PathVariable Long id) {
        OrderResponse response = orderService.trackOrder(Long.valueOf(userId), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Long id,
                                                                        @RequestBody @Valid OrderStatusUpdate update) {
        update.setOrderId(id);
        OrderResponse response = orderService.updateOrderStatus(update);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
