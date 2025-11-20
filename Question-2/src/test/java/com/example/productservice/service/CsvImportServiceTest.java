package com.example.productservice.service;

import com.example.productservice.entity.Category;
import com.example.productservice.entity.Product;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CsvImportServiceTest {

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testImportFromCsv() throws IOException {
        String csvContent = """
                PRODUCT_CODE,PRODUCT_NAME,PRODUCT_CATEGORY_CODE,CATEGORY_CODE,CATEGORY_NAME
                0000000001,Product 01,1,1,Pain Relief
                0000000002,Product 02,1,3,Digestive Health
                0000000003,Product 03,1,5,Baby Care
                """;

        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        CsvImportService.ImportResult result = csvImportService.importFromCsv(csvFile.toString());

        assertThat(result.productsCreated).isEqualTo(3);
        assertThat(result.categoriesCreated).isEqualTo(3);
        assertThat(result.errors).isEqualTo(0);

        List<Product> products = productRepository.findAll();
        List<Category> categories = categoryRepository.findAll();

        assertThat(products).hasSize(3);
        assertThat(categories).hasSize(3);
    }

    @Test
    void testImportWithDuplicates() throws IOException {
        String csvContent = """
                PRODUCT_CODE,PRODUCT_NAME,PRODUCT_CATEGORY_CODE,CATEGORY_CODE,CATEGORY_NAME
                0000000001,Product 01,1,1,Pain Relief
                0000000001,Product 01,1,1,Pain Relief
                0000000002,Product 02,1,1,Pain Relief
                """;

        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        CsvImportService.ImportResult result = csvImportService.importFromCsv(csvFile.toString());

        assertThat(result.productsCreated).isEqualTo(2);
        assertThat(result.productsSkipped).isEqualTo(1);
        assertThat(result.categoriesCreated).isEqualTo(1);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2);
    }

    @Test
    void testImportSkipsExistingProducts() throws IOException {
        Product existingProduct = new Product();
        existingProduct.setProductCode("0000000001");
        existingProduct.setProductName("Existing Product");
        existingProduct.setCategoryCode("1");
        existingProduct.setCreationDate(LocalDateTime.now());
        productRepository.save(existingProduct);

        Category existingCategory = new Category();
        existingCategory.setCategoryCode("1");
        existingCategory.setCategoryName("Existing Category");
        existingCategory.setCreationDate(LocalDateTime.now());
        categoryRepository.save(existingCategory);

        String csvContent = """
                PRODUCT_CODE,PRODUCT_NAME,PRODUCT_CATEGORY_CODE,CATEGORY_CODE,CATEGORY_NAME
                0000000001,Product 01,1,1,Pain Relief
                0000000002,Product 02,1,3,Digestive Health
                """;

        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, csvContent);

        CsvImportService.ImportResult result = csvImportService.importFromCsv(csvFile.toString());

        assertThat(result.productsCreated).isEqualTo(1);
        assertThat(result.productsSkipped).isEqualTo(1);
        assertThat(result.categoriesCreated).isEqualTo(1);
        assertThat(result.categoriesSkipped).isEqualTo(1);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2);
    }
}

