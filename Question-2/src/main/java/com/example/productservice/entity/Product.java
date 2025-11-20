package com.example.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Product Entity - Challenge 1 Requirements:
 * - Unique ID ✓
 * - Product Name ✓
 * - Product Code (unique) ✓
 * - Category Code ✓
 * - Creation Date ✓
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_code", unique = true, nullable = false)
    private String productCode;

    @Column(name = "category_code", nullable = false)
    private String categoryCode;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
    }
}

