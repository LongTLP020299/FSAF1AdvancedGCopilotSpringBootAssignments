package com.fsoft.ecommerce.health;

import com.fsoft.ecommerce.health.PaymentGatewayHealthIndicator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PaymentGatewayHealthIndicator
 */
@ExtendWith(MockitoExtension.class)
public class PaymentGatewayHealthIndicatorTest {

    @InjectMocks
    private PaymentGatewayHealthIndicator paymentGatewayHealthIndicator;

    @Test
    void testCheckHealthReturnsValidStatus() {
        // When
        Map<String, Object> healthStatus = paymentGatewayHealthIndicator.checkHealth();
        
        // Then
        assertNotNull(healthStatus);
        assertTrue(healthStatus.containsKey("status"));
        assertTrue(healthStatus.containsKey("gateway"));
        assertTrue(healthStatus.containsKey("version"));
        assertTrue(healthStatus.containsKey("endpoint"));
        assertTrue(healthStatus.containsKey("lastChecked"));
        
        // Status should be either UP or DOWN
        String status = (String) healthStatus.get("status");
        assertTrue("UP".equals(status) || "DOWN".equals(status));
        
        // Gateway details should be present
        assertEquals("Stripe Payment Gateway", healthStatus.get("gateway"));
        assertEquals("v2.1.0", healthStatus.get("version"));
        assertEquals("https://api.stripe.com/v1/charges", healthStatus.get("endpoint"));
    }

    @Test
    void testCheckHealthContainsTimestamp() {
        // When
        Map<String, Object> healthStatus = paymentGatewayHealthIndicator.checkHealth();
        
        // Then
        assertNotNull(healthStatus.get("lastChecked"));
        String timestamp = (String) healthStatus.get("lastChecked");
        assertFalse(timestamp.isEmpty());
        // Timestamp should contain date and time
        assertTrue(timestamp.contains("T"));
    }

    @Test
    void testCheckHealthMultipleCallsShowDifferentTimestamps() throws InterruptedException {
        // When
        Map<String, Object> firstCheck = paymentGatewayHealthIndicator.checkHealth();
        Thread.sleep(100); // Small delay to ensure different timestamps
        Map<String, Object> secondCheck = paymentGatewayHealthIndicator.checkHealth();
        
        // Then
        String firstTimestamp = (String) firstCheck.get("lastChecked");
        String secondTimestamp = (String) secondCheck.get("lastChecked");
        assertNotEquals(firstTimestamp, secondTimestamp);
    }

    @Test
    void testHealthStatusIncludesOperationalStatus() {
        // When
        Map<String, Object> healthStatus = paymentGatewayHealthIndicator.checkHealth();
        
        // Then
        assertTrue(healthStatus.containsKey("operationalStatus"));
        String operationalStatus = (String) healthStatus.get("operationalStatus");
        assertNotNull(operationalStatus);
        assertFalse(operationalStatus.isEmpty());
    }

    @Test
    void testHealthStatusUpIncludesSuccessMetrics() {
        // When - Keep calling until we get an UP status (since it's random)
        Map<String, Object> healthStatus = null;
        int attempts = 0;
        int maxAttempts = 10;
        
        while (attempts < maxAttempts) {
            healthStatus = paymentGatewayHealthIndicator.checkHealth();
            if ("UP".equals(healthStatus.get("status"))) {
                break;
            }
            attempts++;
        }
        
        // If we got an UP status, verify it has success metrics
        if ("UP".equals(healthStatus.get("status"))) {
            assertTrue(healthStatus.containsKey("responseTime"));
            assertTrue(healthStatus.containsKey("transactionsProcessed"));
            assertTrue(healthStatus.containsKey("successRate"));
            assertEquals("NORMAL", healthStatus.get("operationalStatus"));
        }
    }

    @Test
    void testHealthStatusDownIncludesErrorDetails() {
        // When - Keep calling until we get a DOWN status (since it's random)
        Map<String, Object> healthStatus = null;
        int attempts = 0;
        int maxAttempts = 10;
        
        while (attempts < maxAttempts) {
            healthStatus = paymentGatewayHealthIndicator.checkHealth();
            if ("DOWN".equals(healthStatus.get("status"))) {
                break;
            }
            attempts++;
        }
        
        // If we got a DOWN status, verify it has error details
        if ("DOWN".equals(healthStatus.get("status"))) {
            assertTrue(healthStatus.containsKey("error"));
            assertTrue(healthStatus.containsKey("retryAttempts"));
            assertTrue(healthStatus.containsKey("nextRetryIn"));
            assertEquals("SERVICE_UNAVAILABLE", healthStatus.get("operationalStatus"));
        }
    }
}
