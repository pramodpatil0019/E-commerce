package com.ecommerce.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdate {
    @NotNull
    private Long orderId;

    @NotBlank
    private String status;

    private Long paymentId;
}
