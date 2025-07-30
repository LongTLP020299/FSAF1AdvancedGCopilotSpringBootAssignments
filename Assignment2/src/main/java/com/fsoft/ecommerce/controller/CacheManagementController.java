package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.service.CacheManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

/**
 * Controller for cache management operations
 * Only accessible by admin users
 */
@RestController
@RequestMapping("/api/admin/cache")
@Tag(name = "Cache Management", description = "Admin endpoints for cache management")
@PreAuthorize("hasRole('ADMIN')")
public class CacheManagementController {

    @Autowired
    private CacheManagementService cacheManagementService;

    @Operation(summary = "Get all cache names", description = "Retrieve list of all configured cache names")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cache names retrieved successfully")
    })
    @GetMapping("/names")
    public ResponseEntity<Collection<String>> getCacheNames() {
        return ResponseEntity.ok(cacheManagementService.getCacheNames());
    }

    @Operation(summary = "Clear all caches", description = "Clear all application caches")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All caches cleared successfully")
    })
    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        cacheManagementService.clearAllCaches();
        return ResponseEntity.ok(Map.of("message", "All caches cleared successfully"));
    }

    @Operation(summary = "Clear specific cache", description = "Clear a specific cache by name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cache cleared successfully"),
        @ApiResponse(responseCode = "404", description = "Cache not found")
    })
    @DeleteMapping("/clear/{cacheName}")
    public ResponseEntity<Map<String, String>> clearCache(
            @Parameter(description = "Name of the cache to clear") @PathVariable String cacheName) {
        cacheManagementService.clearCache(cacheName);
        return ResponseEntity.ok(Map.of("message", "Cache '" + cacheName + "' cleared successfully"));
    }

    @Operation(summary = "Clear product caches", description = "Clear all product-related caches")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product caches cleared successfully")
    })
    @DeleteMapping("/clear-products")
    public ResponseEntity<Map<String, String>> clearProductCaches() {
        cacheManagementService.clearProductCaches();
        return ResponseEntity.ok(Map.of("message", "Product caches cleared successfully"));
    }

    @Operation(summary = "Evict product from cache", description = "Remove specific product from cache")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product evicted from cache successfully")
    })
    @DeleteMapping("/evict-product/{productId}")
    public ResponseEntity<Map<String, String>> evictProduct(
            @Parameter(description = "ID of the product to evict") @PathVariable Long productId) {
        cacheManagementService.evictProductFromCache(productId);
        return ResponseEntity.ok(Map.of("message", "Product " + productId + " evicted from cache successfully"));
    }
}
