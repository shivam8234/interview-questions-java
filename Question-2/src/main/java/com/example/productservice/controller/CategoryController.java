package com.example.productservice.controller;

import com.example.productservice.entity.Category;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Category operations.
 * Implements Challenge 2 requirements: Pagination and Sorting.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;


    @GetMapping
    public ResponseEntity<Page<Category>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        // Create pageable with validation (extracted to utility)
        Pageable pageable = PageableUtils.createPageable(page, size, sortBy, sortDirection);
        Page<Category> categories = categoryRepository.findAll(pageable);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        // Validate unique category code (Challenge 1 requirement)
        if (categoryRepository.existsByCategoryCode(category.getCategoryCode())) {
            return ResponseEntity.badRequest().build();
        }

        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }
}

