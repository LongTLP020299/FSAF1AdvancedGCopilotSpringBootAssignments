package com.fsoft.ecommerce.service;

public interface PurchaseVerificationService {
    /**
     * Verify that a user has purchased a specific product with delivered status
     * @param userId the user ID
     * @param productId the product ID
     * @return true if user has purchased the product, false otherwise
     */
    boolean hasUserPurchasedProduct(Long userId, Long productId);
}
