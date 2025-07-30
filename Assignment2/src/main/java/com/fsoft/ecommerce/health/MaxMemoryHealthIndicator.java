package com.fsoft.ecommerce.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * MaxMemoryHealthIndicator implementation
 * 
 * Custom memory health monitoring endpoint that reports DOWN status
 * if used memory exceeds 90% of maximum available memory.
 */
@RestController
public class MaxMemoryHealthIndicator {

    private static final double MEMORY_THRESHOLD = 0.9; // 90%

    /**
     * Custom memory health endpoint under /actuator/health/maxMemory
     * @return Memory health status and details
     */
    @GetMapping("/health/memory")
    public Map<String, Object> getMemoryHealth() {
        Runtime runtime = Runtime.getRuntime();
        
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsagePercentage = (double) usedMemory / maxMemory;
        
        Map<String, Object> healthResponse = new HashMap<>();
        Map<String, Object> details = new HashMap<>();
        
        // Add memory details
        details.put("maxMemory", formatBytes(maxMemory));
        details.put("totalMemory", formatBytes(totalMemory));
        details.put("usedMemory", formatBytes(usedMemory));
        details.put("freeMemory", formatBytes(freeMemory));
        details.put("memoryUsagePercentage", String.format("%.2f%%", memoryUsagePercentage * 100));
        details.put("threshold", String.format("%.0f%%", MEMORY_THRESHOLD * 100));
        
        // Determine status based on threshold
        if (memoryUsagePercentage > MEMORY_THRESHOLD) {
            healthResponse.put("status", "DOWN");
            details.put("message", "Memory usage exceeds threshold");
            details.put("recommendation", "Consider increasing heap size or optimize memory usage");
        } else {
            healthResponse.put("status", "UP");
            details.put("message", "Memory usage is within acceptable limits");
        }
        
        healthResponse.put("details", details);
        return healthResponse;
    }
    
    /**
     * Format bytes into human readable format
     * @param bytes Number of bytes
     * @return Formatted string (e.g., "1.5 GB")
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.1f %s", bytes / Math.pow(1024, exp), pre);
    }
}
