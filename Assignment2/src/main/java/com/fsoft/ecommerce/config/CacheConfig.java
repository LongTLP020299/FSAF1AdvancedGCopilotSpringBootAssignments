package com.fsoft.ecommerce.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Cache configuration for the e-commerce application.
 * Enables caching with a concurrent map-based cache manager for improved performance.
 * 
 * Key Features:
 * - Enables @Cacheable, @CacheEvict, and @CachePut annotations
 * - Pre-configures cache names for better type safety
 * - Supports both entity and DTO caching
 * - Configures cache behavior for different data types
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a concurrent map-based cache manager.
     * This is suitable for single-instance applications and development environments.
     * For production with multiple instances, consider using Redis or other distributed cache solutions.
     *
     * @return CacheManager instance configured with predefined cache names
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Pre-configure cache names for better performance and type safety
        cacheManager.setCacheNames(Arrays.asList(
            "products",           // For paginated product lists
            "product",            // For individual product DTOs (getProductById)
            "product-details",    // For individual product entities (findProductById)
            "productsByCategory", // For category-based product searches
            "searchProducts",     // For name-based product searches
            "advancedSearch",     // For multi-criteria product searches
            "fullTextSearch",     // For full-text search results
            "categories",         // For category lists
            "productStats"        // For product statistics
        ));
        
        // Cache configuration
        cacheManager.setAllowNullValues(false);  // Don't cache null values
        
        return cacheManager;
    }
}
