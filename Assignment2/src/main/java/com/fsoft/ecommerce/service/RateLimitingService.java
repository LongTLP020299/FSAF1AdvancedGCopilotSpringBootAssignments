package com.fsoft.ecommerce.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for handling rate limiting using Bucket4j.
 * Implements rate limiting logic to prevent brute-force attacks.
 * 
 * @author E-commerce Team
 */
@Service
public class RateLimitingService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    // Rate limit configuration: 10 requests per minute
    private static final int LOGIN_REQUESTS_LIMIT = 10;
    private static final Duration LOGIN_REFILL_DURATION = Duration.ofMinutes(1);
    
    /**
     * Creates a bucket for login rate limiting.
     * Allows 10 requests per minute with refill rate.
     * 
     * @return Bucket configured for login rate limiting
     */
    private Bucket createLoginBucket() {
        // Create bandwidth with capacity of 10 tokens, refilled every minute
        Bandwidth bandwidth = Bandwidth.classic(LOGIN_REQUESTS_LIMIT, 
            Refill.intervally(LOGIN_REQUESTS_LIMIT, LOGIN_REFILL_DURATION));
        
        return Bucket4j.builder()
            .addLimit(bandwidth)
            .build();
    }
    
    /**
     * Gets or creates a bucket for the given client identifier (IP address).
     * 
     * @param clientId Client identifier (typically IP address)
     * @return Bucket for the client
     */
    public Bucket getBucket(String clientId) {
        return buckets.computeIfAbsent(clientId, k -> createLoginBucket());
    }
    
    /**
     * Checks if the client can make a login request.
     * 
     * @param clientId Client identifier (typically IP address)
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean canMakeLoginRequest(String clientId) {
        Bucket bucket = getBucket(clientId);
        return bucket.tryConsume(1);
    }
    
    /**
     * Gets the number of available tokens for the client.
     * 
     * @param clientId Client identifier
     * @return Number of available tokens
     */
    public long getAvailableTokens(String clientId) {
        Bucket bucket = getBucket(clientId);
        return bucket.getAvailableTokens();
    }
    
    /**
     * Gets the time until the next token refill for the client.
     * 
     * @param clientId Client identifier
     * @return Duration until next refill in seconds, or 0 if tokens available
     */
    public long getTimeUntilRefillSeconds(String clientId) {
        Bucket bucket = getBucket(clientId);
        try {
            long nanosToWait = bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill();
            return Duration.ofNanos(nanosToWait).getSeconds();
        } catch (Exception e) {
            return 0; // If estimation fails, return 0
        }
    }
    
    /**
     * Resets the rate limit for a specific client.
     * This can be used after successful authentication or for administrative purposes.
     * 
     * @param clientId Client identifier
     */
    public void resetRateLimit(String clientId) {
        buckets.remove(clientId);
    }
    
    /**
     * Gets rate limiting information for monitoring and debugging.
     * 
     * @param clientId Client identifier
     * @return Map containing rate limit information
     */
    public Map<String, Object> getRateLimitInfo(String clientId) {
        Map<String, Object> info = new ConcurrentHashMap<>();
        
        info.put("clientId", clientId);
        info.put("availableTokens", getAvailableTokens(clientId));
        info.put("capacity", LOGIN_REQUESTS_LIMIT);
        info.put("refillPeriod", LOGIN_REFILL_DURATION.toString());
        info.put("timeUntilRefillSeconds", getTimeUntilRefillSeconds(clientId));
        
        return info;
    }
}
