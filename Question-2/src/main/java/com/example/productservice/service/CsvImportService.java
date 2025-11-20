package com.example.productservice.service;

import com.example.productservice.entity.Category;
import com.example.productservice.entity.Product;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * Service for importing product and category data from CSV files.
 * Handles validation, duplicate detection, and database persistence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CsvImportService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ImportResult importFromCsv(String filePath) throws IOException {
        ImportResult result = new ImportResult();

        // Track codes processed in current CSV to avoid duplicate DB calls
        Set<String> processedProductCodes = new HashSet<>();
        Set<String> processedCategoryCodes = new HashSet<>();

        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build())) {

            for (CSVRecord record : csvParser) {
                try {
                    // Extract CSV fields
                    String productCode = record.get("PRODUCT_CODE");
                    String productName = record.get("PRODUCT_NAME");
                    String categoryCode = record.get("CATEGORY_CODE");
                    String categoryName = record.get("CATEGORY_NAME");

                    // Process category first (products reference categories)
                    processCategory(categoryCode, categoryName, processedCategoryCodes, result);

                    // Process product
                    processProduct(productCode, productName, categoryCode, processedProductCodes, result);

                } catch (Exception e) {
                    result.errors++;
                    log.error("Error processing record: {}", record, e);
                }
            }
        }

        log.info("Import completed. Products created: {}, skipped: {}. Categories created: {}, skipped: {}. Errors: {}",
                result.productsCreated, result.productsSkipped,
                result.categoriesCreated, result.categoriesSkipped,
                result.errors);

        return result;
    }

    private void processCategory(String categoryCode, String categoryName,
                                 Set<String> processedCodes, ImportResult result) {
        // Skip if already processed in this CSV import
        if (processedCodes.contains(categoryCode)) {
            return;
        }

        // Check if category exists in database (Challenge 1 requirement)
        if (categoryRepository.existsByCategoryCode(categoryCode)) {
            result.categoriesSkipped++;
            log.debug("Category already exists in database: {}", categoryCode);
        } else {
            // Create new category - @PrePersist will set creationDate automatically
            Category category = new Category();
            category.setCategoryCode(categoryCode);
            category.setCategoryName(categoryName);
            categoryRepository.save(category);
            result.categoriesCreated++;
            log.info("Created category: {}", categoryCode);
        }

        // Mark as processed to avoid duplicate DB calls
        processedCodes.add(categoryCode);
    }

    private void processProduct(String productCode, String productName, String categoryCode,
                               Set<String> processedCodes, ImportResult result) {
        // Skip if already processed in this CSV import (duplicate in CSV)
        if (processedCodes.contains(productCode)) {
            result.productsSkipped++;
            log.debug("Duplicate product in CSV: {}", productCode);
            return;
        }

        // Check if product exists in database (Challenge 1 requirement)
        if (productRepository.existsByProductCode(productCode)) {
            result.productsSkipped++;
            log.debug("Product already exists in database: {}", productCode);
        } else {
            // Create new product - @PrePersist will set creationDate automatically
            Product product = new Product();
            product.setProductCode(productCode);
            product.setProductName(productName);
            product.setCategoryCode(categoryCode);
            productRepository.save(product);
            result.productsCreated++;
            log.info("Created product: {}", productCode);
        }

        // Mark as processed to avoid duplicate DB calls
        processedCodes.add(productCode);
    }

    /**
     * Result object containing statistics from CSV import operation.
     */
    public static class ImportResult {
        public int productsCreated = 0;
        public int productsSkipped = 0;
        public int categoriesCreated = 0;
        public int categoriesSkipped = 0;
        public int errors = 0;

        @Override
        public String toString() {
            return String.format("ImportResult{productsCreated=%d, productsSkipped=%d, categoriesCreated=%d, categoriesSkipped=%d, errors=%d}",
                    productsCreated, productsSkipped, categoriesCreated, categoriesSkipped, errors);
        }
    }
}

