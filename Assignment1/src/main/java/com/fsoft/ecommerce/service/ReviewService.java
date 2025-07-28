package com.fsoft.ecommerce.service;

import com.fsoft.ecommerce.dto.ReviewDTO;
import com.fsoft.ecommerce.entity.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(ReviewDTO reviewDTO, Long userId, Long productId);
    List<Review> getReviewsByProduct(Long productId);
    List<Review> getReviewsByUser(Long userId);
    void updateProductAverageRating(Long productId);
}
