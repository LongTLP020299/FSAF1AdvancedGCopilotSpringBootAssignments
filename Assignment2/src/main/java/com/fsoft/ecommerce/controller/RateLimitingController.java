package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.service.RateLimitingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for managing rate limiting functionality.
 * Provides endpoints for monitoring and managing rate limits.
 * 
 * @author E-commerce Team
 */
@RestController
@RequestMapping("/api/rate-limit")
@Tag(name = "Rate Limiting", description = "Rate limiting management and monitoring")
public class RateLimitingController {

    @Autowired
    private RateLimitingService rateLimitingService;
    
    /**
     * Get rate limiting information for the current client.
     * 
     * @param request HTTP request to extract client IP
     * @return Rate limiting information
     */
    @GetMapping("/status")
    @Operation(summary = "Get rate limiting status", 
               description = "Returns current rate limiting status for the requesting client")
    public ResponseEntity<Map<String, Object>> getRateLimitStatus(HttpServletRequest request) {
        String clientId = getClientId(request);
        Map<String, Object> rateLimitInfo = rateLimitingService.getRateLimitInfo(clientId);
        return ResponseEntity.ok(rateLimitInfo);
    }
    
    /**
     * Get rate limiting information for a specific client.
     * Requires ADMIN role.
     * 
     * @param clientId Client identifier (IP address)
     * @return Rate limiting information
     */
    @GetMapping("/status/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get rate limiting status for specific client", 
               description = "Returns rate limiting status for a specific client (admin only)")
    public ResponseEntity<Map<String, Object>> getRateLimitStatusForClient(
            @Parameter(description = "Client IP address") @PathVariable String clientId) {
        Map<String, Object> rateLimitInfo = rateLimitingService.getRateLimitInfo(clientId);
        return ResponseEntity.ok(rateLimitInfo);
    }
    
    /**
     * Reset rate limiting for a specific client.
     * Requires ADMIN role.
     * 
     * @param clientId Client identifier (IP address)
     * @return Success message
     */
    @PostMapping("/reset/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reset rate limit for specific client", 
               description = "Resets the rate limit for a specific client (admin only)")
    public ResponseEntity<Map<String, String>> resetRateLimitForClient(
            @Parameter(description = "Client IP address") @PathVariable String clientId) {
        rateLimitingService.resetRateLimit(clientId);
        return ResponseEntity.ok(Map.of(
            "message", "Rate limit reset successfully",
            "clientId", clientId
        ));
    }
    
    /**
     * Reset rate limiting for the current client.
     * 
     * @param request HTTP request to extract client IP
     * @return Success message
     */
    @PostMapping("/reset")
    @Operation(summary = "Reset rate limit for current client", 
               description = "Resets the rate limit for the requesting client")
    public ResponseEntity<Map<String, String>> resetRateLimit(HttpServletRequest request) {
        String clientId = getClientId(request);
        rateLimitingService.resetRateLimit(clientId);
        return ResponseEntity.ok(Map.of(
            "message", "Rate limit reset successfully",
            "clientId", clientId
        ));
    }
    
    /**
     * Check if the current client can make a login request.
     * 
     * @param request HTTP request to extract client IP
     * @return Whether login request is allowed
     */
    @GetMapping("/can-login")
    @Operation(summary = "Check if login is allowed", 
               description = "Checks if the requesting client can make a login request")
    public ResponseEntity<Map<String, Object>> canMakeLoginRequest(HttpServletRequest request) {
        String clientId = getClientId(request);
        boolean canLogin = rateLimitingService.canMakeLoginRequest(clientId);
        
        Map<String, Object> response = Map.of(
            "clientId", clientId,
            "canLogin", canLogin,
            "remainingTokens", rateLimitingService.getAvailableTokens(clientId),
            "timeUntilRefillSeconds", rateLimitingService.getTimeUntilRefillSeconds(clientId)
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Extracts client identifier from the request.
     * Prioritizes X-Forwarded-For header for proxy scenarios,
     * falls back to X-Real-IP, then remote address.
     * 
     * @param request HTTP request
     * @return Client identifier (IP address)
     */
    private String getClientId(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}
