package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.health.PaymentGatewayHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for custom health checks.
 * Provides endpoints to check the health status of various system components.
 * 
 * @author E-commerce Team
 */
@RestController
@RequestMapping("/api/health")
public class CustomHealthController {

    @Autowired
    private PaymentGatewayHealthIndicator paymentGatewayHealthIndicator;

    /**
     * Get payment gateway health status.
     * 
     * @return ResponseEntity containing payment gateway health details
     */
    @GetMapping("/payment-gateway")
    public ResponseEntity<Map<String, Object>> getPaymentGatewayHealth() {
        Map<String, Object> healthStatus = paymentGatewayHealthIndicator.checkHealth();
        
        // Return appropriate HTTP status based on health check result
        String status = (String) healthStatus.get("status");
        if ("UP".equals(status)) {
            return ResponseEntity.ok(healthStatus);
        } else {
            return ResponseEntity.status(503).body(healthStatus); // Service Unavailable
        }
    }

    /**
     * Get overall system health status including payment gateway.
     * 
     * @return ResponseEntity containing overall system health
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> systemHealth = new HashMap<>();
        
        // Check payment gateway
        Map<String, Object> paymentGatewayHealth = paymentGatewayHealthIndicator.checkHealth();
        systemHealth.put("paymentGateway", paymentGatewayHealth);
        
        // Add other system components health here in the future
        systemHealth.put("database", createDatabaseHealth());
        systemHealth.put("cache", createCacheHealth());
        
        // Determine overall status
        boolean allHealthy = isSystemHealthy(systemHealth);
        systemHealth.put("status", allHealthy ? "UP" : "DOWN");
        systemHealth.put("timestamp", java.time.LocalDateTime.now().toString());
        
        if (allHealthy) {
            return ResponseEntity.ok(systemHealth);
        } else {
            return ResponseEntity.status(503).body(systemHealth);
        }
    }

    /**
     * Creates mock database health status.
     * In production, this would check actual database connectivity.
     */
    private Map<String, Object> createDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        dbHealth.put("status", "UP");
        dbHealth.put("database", "MySQL");
        dbHealth.put("validationQuery", "SELECT 1");
        return dbHealth;
    }

    /**
     * Creates mock cache health status.
     * In production, this would check actual cache connectivity.
     */
    private Map<String, Object> createCacheHealth() {
        Map<String, Object> cacheHealth = new HashMap<>();
        cacheHealth.put("status", "UP");
        cacheHealth.put("provider", "ConcurrentMapCacheManager");
        cacheHealth.put("caches", new String[]{"product-details", "products", "searchProducts"});
        return cacheHealth;
    }

    /**
     * Determines if the overall system is healthy based on component statuses.
     */
    private boolean isSystemHealthy(Map<String, Object> systemHealth) {
        for (Object component : systemHealth.values()) {
            if (component instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> componentHealth = (Map<String, Object>) component;
                String status = (String) componentHealth.get("status");
                if (!"UP".equals(status)) {
                    return false;
                }
            }
        }
        return true;
    }
}
