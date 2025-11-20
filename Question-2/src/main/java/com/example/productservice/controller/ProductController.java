package com.example.productservice.controller;

import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Product operations.
 * Implements Challenge 2 requirements: Pagination, Sorting, and Search by product code.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(required = false) String productCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        // Create pageable with validation (extracted to utility)
        Pageable pageable = PageableUtils.createPageable(page, size, sortBy, sortDirection);

        // Search by product code if provided, otherwise return all (Challenge 2 requirement)
        Page<Product> products = (productCode != null && !productCode.isEmpty())
                ? productRepository.findByProductCodeContaining(productCode, pageable)
                : productRepository.findAll(pageable);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // Validate unique product code (Challenge 1 requirement)
        if (productRepository.existsByProductCode(product.getProductCode())) {
            return ResponseEntity.badRequest().build();
        }

        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }
}

