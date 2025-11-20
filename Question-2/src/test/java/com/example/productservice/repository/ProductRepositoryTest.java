package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        
        testProduct = new Product();
        testProduct.setProductCode("TEST001");
        testProduct.setProductName("Test Product");
        testProduct.setCategoryCode("CAT001");
        testProduct.setCreationDate(LocalDateTime.now());
    }

    @Test
    void testSaveProduct() {
        Product saved = productRepository.save(testProduct);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProductCode()).isEqualTo("TEST001");
        assertThat(saved.getProductName()).isEqualTo("Test Product");
    }

    @Test
    void testFindByProductCode() {
        productRepository.save(testProduct);
        
        Optional<Product> found = productRepository.findByProductCode("TEST001");
        
        assertThat(found).isPresent();
        assertThat(found.get().getProductName()).isEqualTo("Test Product");
    }

    @Test
    void testExistsByProductCode() {
        productRepository.save(testProduct);
        
        boolean exists = productRepository.existsByProductCode("TEST001");
        boolean notExists = productRepository.existsByProductCode("NONEXISTENT");
        
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByProductCodeContaining() {
        productRepository.save(testProduct);
        
        Product product2 = new Product();
        product2.setProductCode("TEST002");
        product2.setProductName("Test Product 2");
        product2.setCategoryCode("CAT001");
        product2.setCreationDate(LocalDateTime.now());
        productRepository.save(product2);
        
        Page<Product> results = productRepository.findByProductCodeContaining("TEST", PageRequest.of(0, 10));
        
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testUniqueProductCode() {
        productRepository.save(testProduct);
        
        Product duplicate = new Product();
        duplicate.setProductCode("TEST001");
        duplicate.setProductName("Duplicate Product");
        duplicate.setCategoryCode("CAT002");
        duplicate.setCreationDate(LocalDateTime.now());
        
        try {
            productRepository.save(duplicate);
            productRepository.flush();
            assertThat(false).isTrue(); // Should not reach here
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }
}

