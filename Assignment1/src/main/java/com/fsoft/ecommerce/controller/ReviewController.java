package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.dto.ReviewDTO;
import com.fsoft.ecommerce.entity.Review;
import com.fsoft.ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping("/products/{productId}/reviews")
    @PreAuthorize("hasRole('USER')") // Only authenticated users can add reviews
    public ResponseEntity<Review> addReview(@PathVariable @Min(1) @NotNull Long productId,
                                          @Valid @RequestBody ReviewDTO reviewDTO) {
        // Get current authenticated user ID instead of accepting it as parameter
        Long currentUserId = getCurrentUserId();
        Review review = reviewService.addReview(reviewDTO, currentUserId, productId);
        return ResponseEntity.ok(review);
    }
    
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable @Min(1) @NotNull Long productId) {
        List<Review> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Get current authenticated user ID
     * For development, return default user ID (1L)
     * In production, extract from JWT token or session
     */
    private Long getCurrentUserId() {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // For development - return fixed user ID
        // In production, extract from authentication.getPrincipal()
        return 1L; // TODO: Implement proper user ID extraction from JWT/session
    }
}
