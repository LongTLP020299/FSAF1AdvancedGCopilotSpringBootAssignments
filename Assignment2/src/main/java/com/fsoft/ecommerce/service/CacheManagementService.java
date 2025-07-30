package com.fsoft.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Service for managing cache operations
 * Provides utility methods for cache management and monitoring
 */
@Service
public class CacheManagementService {

    @Autowired
    private CacheManager cacheManager;

    /**
     * Clears all caches
     */
    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    /**
     * Clears a specific cache by name
     * 
     * @param cacheName the name of the cache to clear
     */
    public void clearCache(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Clears product-related caches
     * Useful when product data changes significantly
     */
    public void clearProductCaches() {
        clearCache("products");
        clearCache("product");
        clearCache("product-details");
        clearCache("productsByCategory");
        clearCache("searchProducts");
        clearCache("advancedSearch");
        clearCache("fullTextSearch");
    }

    /**
     * Gets all cache names
     * 
     * @return collection of cache names
     */
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    /**
     * Evicts a specific entry from product-details cache
     * 
     * @param productId the product ID to evict
     */
    public void evictProductFromCache(Long productId) {
        var productCache = cacheManager.getCache("product");
        var productDetailsCache = cacheManager.getCache("product-details");
        
        if (productCache != null) {
            productCache.evict(productId);
        }
        
        if (productDetailsCache != null) {
            productDetailsCache.evict(productId);
        }
    }
}
