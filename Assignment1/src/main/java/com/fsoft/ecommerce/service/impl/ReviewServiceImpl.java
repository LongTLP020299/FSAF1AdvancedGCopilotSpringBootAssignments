package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.ReviewDTO;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.entity.Review;
import com.fsoft.ecommerce.entity.User;
import com.fsoft.ecommerce.exception.DuplicateReviewException;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.exception.UserNotFoundException;
import com.fsoft.ecommerce.exception.UserNotPurchasedProductException;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.repository.ReviewRepository;
import com.fsoft.ecommerce.repository.UserRepository;
import com.fsoft.ecommerce.service.ProductRatingService;
import com.fsoft.ecommerce.service.PurchaseVerificationService;
import com.fsoft.ecommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PurchaseVerificationService purchaseVerificationService;
    
    @Autowired
    private ProductRatingService productRatingService;
    
    @Override
    public Review addReview(ReviewDTO reviewDTO, Long userId, Long productId) {
        // Input validation
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        // 1. Verify user has purchased the product with delivered status (delegated to service)
        boolean hasPurchased = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        if (!hasPurchased) {
            throw new UserNotPurchasedProductException(
                "User must purchase the product before reviewing it");
        }
        
        // 2. Check if user already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateReviewException(
                "User has already reviewed this product");
        }
        
        // 3. Fetch user and product entities with consistent exception handling
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // 4. Create and save review
        Review review = new Review(user, product, reviewDTO.getRating(), reviewDTO.getComment());
        Review savedReview = reviewRepository.save(review);
        
        // 5. Update product average rating (delegated to service)
        productRatingService.updateProductAverageRating(productId);
        
        return savedReview;
    }
    
    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        // Validate product exists
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
    
    @Override
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    @Override
    public void updateProductAverageRating(Long productId) {
        productRatingService.updateProductAverageRating(productId);
    }
}
