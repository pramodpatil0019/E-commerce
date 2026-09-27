package com.ecommerce.cartservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    @ToString.Exclude
    private Cart cart;

    @Column(nullable = false)
    private Long productId;

    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
}
