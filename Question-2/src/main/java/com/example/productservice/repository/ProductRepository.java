package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByProductCode(String productCode);
    
    boolean existsByProductCode(String productCode);
    
    Page<Product> findByProductCodeContaining(String productCode, Pageable pageable);
}

