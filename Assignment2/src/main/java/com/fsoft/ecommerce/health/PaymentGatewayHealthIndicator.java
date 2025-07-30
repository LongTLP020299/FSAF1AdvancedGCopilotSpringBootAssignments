package com.fsoft.ecommerce.health;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Custom Health Indicator for Payment Gateway status.
 * In production, this would call the actual payment provider's health endpoint.
 * Currently simulates random UP/DOWN status for demonstration.
 * 
 * @author E-commerce Team
 */
@Component("paymentGateway")
public class PaymentGatewayHealthIndicator {

    private final Random random = new Random();
    private static final String PAYMENT_GATEWAY_NAME = "Stripe Payment Gateway";
    private static final String GATEWAY_VERSION = "v2.1.0";
    private static final String GATEWAY_URL = "https://api.stripe.com/v1/charges";
    
    /**
     * Checks the health status of the payment gateway.
     * 
     * @return Map containing health status and details
     */
    public Map<String, Object> checkHealth() {
        Map<String, Object> healthStatus = new HashMap<>();
        
        try {
            // Simulate checking payment gateway status
            boolean isHealthy = checkPaymentGatewayStatus();
            
            healthStatus.put("status", isHealthy ? "UP" : "DOWN");
            healthStatus.put("gateway", PAYMENT_GATEWAY_NAME);
            healthStatus.put("version", GATEWAY_VERSION);
            healthStatus.put("endpoint", GATEWAY_URL);
            healthStatus.put("lastChecked", getCurrentTimestamp());
            
            if (isHealthy) {
                healthStatus.put("responseTime", "120ms");
                healthStatus.put("transactionsProcessed", getRandomTransactionCount());
                healthStatus.put("successRate", "99.8%");
                healthStatus.put("operationalStatus", "NORMAL");
            } else {
                healthStatus.put("error", "Connection timeout after 5000ms");
                healthStatus.put("retryAttempts", 3);
                healthStatus.put("nextRetryIn", "30 seconds");
                healthStatus.put("operationalStatus", "SERVICE_UNAVAILABLE");
            }
            
        } catch (Exception e) {
            healthStatus.put("status", "DOWN");
            healthStatus.put("gateway", PAYMENT_GATEWAY_NAME);
            healthStatus.put("error", "Unexpected error: " + e.getMessage());
            healthStatus.put("lastChecked", getCurrentTimestamp());
            healthStatus.put("operationalStatus", "ERROR");
        }
        
        return healthStatus;
    }

    /**
     * Simulates calling the payment gateway's health endpoint.
     * In production, this would make an actual HTTP call to check gateway status.
     * 
     * @return true if gateway is healthy, false otherwise
     */
    private boolean checkPaymentGatewayStatus() {
        // Simulate network delay
        try {
            Thread.sleep(100); // Simulate 100ms response time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // Simulate 80% uptime (80% chance of returning true)
        return random.nextInt(100) < 80;
    }

    /**
     * Gets current timestamp in ISO format.
     * 
     * @return formatted timestamp string
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Simulates random transaction count for demonstration.
     * 
     * @return random transaction count between 1000-5000
     */
    private int getRandomTransactionCount() {
        return 1000 + random.nextInt(4000);
    }
}
