package com.ecommerce.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    private BigDecimal price;
    
    private Integer stockQuantity = 0;
    
    private String category;
    
    private String imageUrl;
    
    private String specifications;
}
