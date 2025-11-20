package com.example.productservice.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class for creating Pageable objects with sorting.
 * Eliminates duplicate code across controllers.
 */
public class PageableUtils {

    private PageableUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a Pageable object with pagination and sorting parameters.
     * 
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param sortBy Field name to sort by
     * @param sortDirection Sort direction ("ASC" or "DESC")
     * @return Pageable object configured with the specified parameters
     */
    public static Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        // Validate page and size parameters
        int validatedPage = Math.max(0, page);
        int validatedSize = Math.max(1, Math.min(size, 100)); // Cap at 100 items per page
        
        // Create sort object based on direction
        Sort sort = sortDirection.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        return PageRequest.of(validatedPage, validatedSize, sort);
    }
}

