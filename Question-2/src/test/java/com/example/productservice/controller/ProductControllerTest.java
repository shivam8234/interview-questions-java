package com.example.productservice.controller;

import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void testGetProducts() throws Exception {
        // Create test products
        Product product1 = new Product();
        product1.setProductCode("TEST001");
        product1.setProductName("Test Product 1");
        product1.setCategoryCode("CAT001");
        product1.setCreationDate(LocalDateTime.now());
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setProductCode("TEST002");
        product2.setProductName("Test Product 2");
        product2.setCategoryCode("CAT002");
        product2.setCreationDate(LocalDateTime.now());
        productRepository.save(product2);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    void testGetProductsWithPagination() throws Exception {
        // Create 15 test products
        for (int i = 1; i <= 15; i++) {
            Product product = new Product();
            product.setProductCode(String.format("TEST%03d", i));
            product.setProductName("Test Product " + i);
            product.setCategoryCode("CAT001");
            product.setCreationDate(LocalDateTime.now());
            productRepository.save(product);
        }

        // Get first page
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", is(15)))
                .andExpect(jsonPath("$.totalPages", is(2)));

        // Get second page
        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", is(15)));
    }

    @Test
    void testGetProductsWithSorting() throws Exception {
        Product product1 = new Product();
        product1.setProductCode("AAAA");
        product1.setProductName("Product A");
        product1.setCategoryCode("CAT001");
        product1.setCreationDate(LocalDateTime.now());
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setProductCode("ZZZZ");
        product2.setProductName("Product Z");
        product2.setCategoryCode("CAT002");
        product2.setCreationDate(LocalDateTime.now());
        productRepository.save(product2);

        // Sort ascending
        mockMvc.perform(get("/api/products")
                        .param("sortBy", "productCode")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productCode", is("AAAA")))
                .andExpect(jsonPath("$.content[1].productCode", is("ZZZZ")));

        // Sort descending
        mockMvc.perform(get("/api/products")
                        .param("sortBy", "productCode")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productCode", is("ZZZZ")))
                .andExpect(jsonPath("$.content[1].productCode", is("AAAA")));
    }

    @Test
    void testSearchByProductCode() throws Exception {
        Product product1 = new Product();
        product1.setProductCode("ABC123");
        product1.setProductName("Product ABC");
        product1.setCategoryCode("CAT001");
        product1.setCreationDate(LocalDateTime.now());
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setProductCode("XYZ789");
        product2.setProductName("Product XYZ");
        product2.setCategoryCode("CAT002");
        product2.setCreationDate(LocalDateTime.now());
        productRepository.save(product2);

        mockMvc.perform(get("/api/products")
                        .param("productCode", "ABC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].productCode", is("ABC123")));
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = new Product();
        product.setProductCode("TEST001");
        product.setProductName("Test Product");
        product.setCategoryCode("CAT001");
        product.setCreationDate(LocalDateTime.now());
        Product saved = productRepository.save(product);

        mockMvc.perform(get("/api/products/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode", is("TEST001")))
                .andExpect(jsonPath("$.productName", is("Test Product")));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setProductCode("NEW001");
        product.setProductName("New Product");
        product.setCategoryCode("CAT001");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productCode", is("NEW001")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void testCreateProductWithDuplicateCode() throws Exception {
        Product existing = new Product();
        existing.setProductCode("DUP001");
        existing.setProductName("Existing Product");
        existing.setCategoryCode("CAT001");
        existing.setCreationDate(LocalDateTime.now());
        productRepository.save(existing);

        Product duplicate = new Product();
        duplicate.setProductCode("DUP001");
        duplicate.setProductName("Duplicate Product");
        duplicate.setCategoryCode("CAT002");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest());
    }
}

