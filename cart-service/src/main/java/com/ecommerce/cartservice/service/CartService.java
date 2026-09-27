package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.*;
import com.ecommerce.cartservice.exception.BadRequestException;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;

    @Cacheable(value = "carts", key = "#userId")
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        return mapToCartResponse(cart);
    }

    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        ProductDto product;
        try {
            ResponseEntity<ApiResponse<ProductDto>> response = restTemplate.exchange(
                    "http://product-service/api/products/" + request.getProductId(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<ProductDto>>() {}
            );
            if (response.getBody() == null || !response.getBody().isSuccess()) {
                throw new BadRequestException("Product not found or product service unavailable");
            }
            product = response.getBody().getData();
        } catch (Exception e) {
            throw new BadRequestException("Product not found or product service unavailable");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(request.getQuantity())
                    .imageUrl(product.getImageUrl())
                    .build();
            cart.addItem(newItem);
        }

        return mapToCartResponse(cartRepository.save(cart));
    }

    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Item not found in cart"));

        item.setQuantity(request.getQuantity());
        return mapToCartResponse(cartRepository.save(cart));
    }

    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse removeCartItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Cart not found"));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Item not found in cart"));

        cart.removeItem(itemToRemove);
        return mapToCartResponse(cartRepository.save(cart));
    }

    @CacheEvict(value = "carts", key = "#userId")
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Cart not found"));
        cart.clearItems();
        cartRepository.save(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream().map(item ->
                CartItemDto.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .imageUrl(item.getImageUrl())
                        .subtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                        .build()
        ).collect(Collectors.toList());

        return CartResponse.builder()
                .userId(cart.getUserId())
                .items(itemDtos)
                .totalPrice(calculateTotalPrice(cart.getItems()))
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private BigDecimal calculateTotalPrice(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
