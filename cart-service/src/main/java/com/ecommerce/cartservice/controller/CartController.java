package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.AddToCartRequest;
import com.ecommerce.cartservice.dto.ApiResponse;
import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.dto.UpdateCartItemRequest;
import com.ecommerce.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart(@RequestHeader("X-User-Id") String userId) {
        return ApiResponse.success(cartService.getCart(Long.valueOf(userId)));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addToCart(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid AddToCartRequest request) {
        return ApiResponse.success("Item added to cart", cartService.addToCart(Long.valueOf(userId), request));
    }

    @PutMapping("/items/{productId}")
    public ApiResponse<CartResponse> updateCartItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long productId,
            @RequestBody @Valid UpdateCartItemRequest request) {
        return ApiResponse.success("Cart updated", cartService.updateCartItem(Long.valueOf(userId), productId, request));
    }

    @DeleteMapping("/items/{productId}")
    public ApiResponse<CartResponse> removeCartItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long productId) {
        return ApiResponse.success("Item removed", cartService.removeCartItem(Long.valueOf(userId), productId));
    }

    @DeleteMapping
    public ApiResponse<String> clearCart(@RequestHeader("X-User-Id") String userId) {
        cartService.clearCart(Long.valueOf(userId));
        return ApiResponse.success("Cart cleared", null);
    }
}
