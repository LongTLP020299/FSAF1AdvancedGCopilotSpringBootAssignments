package com.fsoft.ecommerce.service;

public interface ProductRatingService {
    /**
     * Update the average rating for a product based on all its reviews
     * @param productId the product ID to update rating for
     */
    void updateProductAverageRating(Long productId);
}
