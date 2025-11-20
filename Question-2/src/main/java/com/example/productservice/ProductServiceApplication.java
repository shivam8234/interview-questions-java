package com.example.productservice;

import com.example.productservice.service.CsvImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
@Slf4j
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner importData(@Autowired(required = false) CsvImportService csvImportService) {
        return args -> {
            if (csvImportService == null) {
                log.info("CsvImportService not available, skipping CSV import");
                return;
            }

            String csvFilePath = "TestExampleFile.csv";
            File csvFile = new File(csvFilePath);

            if (csvFile.exists()) {
                log.info("Starting CSV import from: {}", csvFilePath);
                CsvImportService.ImportResult result = csvImportService.importFromCsv(csvFilePath);
                log.info("CSV import completed: {}", result);
            } else {
                log.warn("CSV file not found at: {}. Skipping import.", csvFilePath);
            }
        };
    }
}

