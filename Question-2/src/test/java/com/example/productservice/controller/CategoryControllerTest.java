package com.example.productservice.controller;

import com.example.productservice.entity.Category;
import com.example.productservice.repository.CategoryRepository;
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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void testGetCategories() throws Exception {
        Category category1 = new Category();
        category1.setCategoryCode("CAT001");
        category1.setCategoryName("Category 1");
        category1.setCreationDate(LocalDateTime.now());
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setCategoryCode("CAT002");
        category2.setCategoryName("Category 2");
        category2.setCreationDate(LocalDateTime.now());
        categoryRepository.save(category2);

        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    void testGetCategoriesWithPagination() throws Exception {
        for (int i = 1; i <= 15; i++) {
            Category category = new Category();
            category.setCategoryCode("CAT" + String.format("%03d", i));
            category.setCategoryName("Category " + i);
            category.setCreationDate(LocalDateTime.now());
            categoryRepository.save(category);
        }

        mockMvc.perform(get("/api/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", is(15)))
                .andExpect(jsonPath("$.totalPages", is(2)));
    }

    @Test
    void testGetCategoriesWithSorting() throws Exception {
        Category category1 = new Category();
        category1.setCategoryCode("AAA");
        category1.setCategoryName("Category A");
        category1.setCreationDate(LocalDateTime.now());
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setCategoryCode("ZZZ");
        category2.setCategoryName("Category Z");
        category2.setCreationDate(LocalDateTime.now());
        categoryRepository.save(category2);

        mockMvc.perform(get("/api/categories")
                        .param("sortBy", "categoryCode")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].categoryCode", is("AAA")))
                .andExpect(jsonPath("$.content[1].categoryCode", is("ZZZ")));
    }

    @Test
    void testGetCategoryById() throws Exception {
        Category category = new Category();
        category.setCategoryCode("CAT001");
        category.setCategoryName("Test Category");
        category.setCreationDate(LocalDateTime.now());
        Category saved = categoryRepository.save(category);

        mockMvc.perform(get("/api/categories/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryCode", is("CAT001")))
                .andExpect(jsonPath("$.categoryName", is("Test Category")));
    }

    @Test
    void testGetCategoryByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCategory() throws Exception {
        Category category = new Category();
        category.setCategoryCode("NEW001");
        category.setCategoryName("New Category");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryCode", is("NEW001")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void testCreateCategoryWithDuplicateCode() throws Exception {
        Category existing = new Category();
        existing.setCategoryCode("DUP001");
        existing.setCategoryName("Existing Category");
        existing.setCreationDate(LocalDateTime.now());
        categoryRepository.save(existing);

        Category duplicate = new Category();
        duplicate.setCategoryCode("DUP001");
        duplicate.setCategoryName("Duplicate Category");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest());
    }
}

