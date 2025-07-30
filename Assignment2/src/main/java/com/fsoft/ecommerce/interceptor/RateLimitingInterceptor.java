package com.fsoft.ecommerce.interceptor;

import com.fsoft.ecommerce.service.RateLimitingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for rate limiting specific endpoints.
 * Currently focuses on login endpoint to prevent brute-force attacks.
 * 
 * @author E-commerce Team
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingInterceptor.class);
    
    @Autowired
    private RateLimitingService rateLimitingService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Apply rate limiting only to login endpoint
        if ("/api/auth/login".equals(requestURI) && "POST".equals(method)) {
            String clientId = getClientId(request);
            
            logger.debug("Checking rate limit for client: {} on endpoint: {}", clientId, requestURI);
            
            if (!rateLimitingService.canMakeLoginRequest(clientId)) {
                logger.warn("Rate limit exceeded for client: {} on login endpoint", clientId);
                
                // Set response headers with rate limit information
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setHeader("X-RateLimit-Limit", "10");
                response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitingService.getAvailableTokens(clientId)));
                response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitingService.getTimeUntilRefillSeconds(clientId)));
                
                // Write JSON response
                String jsonResponse = String.format(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Please try again in %d seconds.\",\"code\":%d}",
                    rateLimitingService.getTimeUntilRefillSeconds(clientId),
                    HttpStatus.TOO_MANY_REQUESTS.value()
                );
                response.getWriter().write(jsonResponse);
                
                return false; // Block the request
            }
            
            // Add rate limit headers for successful requests
            response.setHeader("X-RateLimit-Limit", "10");
            response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitingService.getAvailableTokens(clientId)));
            
            logger.debug("Rate limit check passed for client: {} on login endpoint. Remaining tokens: {}", 
                        clientId, rateLimitingService.getAvailableTokens(clientId));
        }
        
        return true; // Allow the request to proceed
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
