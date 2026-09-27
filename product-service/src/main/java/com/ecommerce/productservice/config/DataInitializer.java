package com.ecommerce.productservice.config;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            List<Product> products = List.of(
                    Product.builder().name("Laptop Pro 15").description("High-performance laptop for professionals").price(new BigDecimal("1299.99")).stockQuantity(50).category("Electronics").imageUrl("laptop.jpg").specifications("16GB RAM, 512GB SSD").build(),
                    Product.builder().name("Wireless Mouse").description("Ergonomic wireless mouse").price(new BigDecimal("29.99")).stockQuantity(150).category("Electronics").imageUrl("mouse.jpg").specifications("Bluetooth 5.0, 4000 DPI").build(),
                    Product.builder().name("Cotton T-Shirt").description("Comfortable 100% cotton t-shirt").price(new BigDecimal("19.99")).stockQuantity(200).category("Clothing").imageUrl("tshirt.jpg").specifications("Size: M, Color: Blue").build(),
                    Product.builder().name("Running Shoes").description("Lightweight shoes for running").price(new BigDecimal("89.99")).stockQuantity(100).category("Clothing").imageUrl("shoes.jpg").specifications("Size: 10, Color: Red").build(),
                    Product.builder().name("Java Programming").description("Comprehensive guide to Java").price(new BigDecimal("45.00")).stockQuantity(75).category("Books").imageUrl("java_book.jpg").specifications("Author: John Doe, Pages: 500").build(),
                    Product.builder().name("Spring Boot in Action").description("Learn Spring Boot effectively").price(new BigDecimal("55.00")).stockQuantity(60).category("Books").imageUrl("spring_book.jpg").specifications("Author: Jane Doe, Pages: 400").build(),
                    Product.builder().name("Coffee Maker").description("Programmable coffee maker").price(new BigDecimal("79.99")).stockQuantity(40).category("Home & Kitchen").imageUrl("coffee_maker.jpg").specifications("Capacity: 12 cups").build(),
                    Product.builder().name("Blender").description("High-speed blender for smoothies").price(new BigDecimal("49.99")).stockQuantity(80).category("Home & Kitchen").imageUrl("blender.jpg").specifications("Power: 600W").build(),
                    Product.builder().name("Yoga Mat").description("Non-slip yoga mat").price(new BigDecimal("25.00")).stockQuantity(120).category("Sports").imageUrl("yoga_mat.jpg").specifications("Thickness: 6mm").build(),
                    Product.builder().name("Dumbbell Set").description("Adjustable dumbbell set").price(new BigDecimal("119.99")).stockQuantity(30).category("Sports").imageUrl("dumbbells.jpg").specifications("Weight: 5-50 lbs").build()
            );
            productRepository.saveAll(products);
        }
    }
}
