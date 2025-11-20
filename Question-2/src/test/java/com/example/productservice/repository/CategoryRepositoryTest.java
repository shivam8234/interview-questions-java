package com.example.productservice.repository;

import com.example.productservice.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        
        testCategory = new Category();
        testCategory.setCategoryCode("CAT001");
        testCategory.setCategoryName("Test Category");
        testCategory.setCreationDate(LocalDateTime.now());
    }

    @Test
    void testSaveCategory() {
        Category saved = categoryRepository.save(testCategory);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCategoryCode()).isEqualTo("CAT001");
        assertThat(saved.getCategoryName()).isEqualTo("Test Category");
    }

    @Test
    void testFindByCategoryCode() {
        categoryRepository.save(testCategory);
        
        Optional<Category> found = categoryRepository.findByCategoryCode("CAT001");
        
        assertThat(found).isPresent();
        assertThat(found.get().getCategoryName()).isEqualTo("Test Category");
    }

    @Test
    void testExistsByCategoryCode() {
        categoryRepository.save(testCategory);
        
        boolean exists = categoryRepository.existsByCategoryCode("CAT001");
        boolean notExists = categoryRepository.existsByCategoryCode("NONEXISTENT");
        
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testUniqueCategoryCode() {
        categoryRepository.save(testCategory);
        
        Category duplicate = new Category();
        duplicate.setCategoryCode("CAT001");
        duplicate.setCategoryName("Duplicate Category");
        duplicate.setCreationDate(LocalDateTime.now());
        
        try {
            categoryRepository.save(duplicate);
            categoryRepository.flush();
            assertThat(false).isTrue(); // Should not reach here
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }
}

