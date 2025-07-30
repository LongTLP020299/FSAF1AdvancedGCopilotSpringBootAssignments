package com.fsoft.ecommerce.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration for caching to ensure cache works properly in tests.
 * 
 * @author E-commerce Team
 */
@TestConfiguration
@EnableCaching
public class TestCacheConfig {

    @Bean
    @Primary
    public CacheManager testCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "product", 
            "product-details", 
            "products", 
            "productsByCategory", 
            "searchProducts", 
            "advancedSearch",
            "categories", 
            "users"
        ));
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
