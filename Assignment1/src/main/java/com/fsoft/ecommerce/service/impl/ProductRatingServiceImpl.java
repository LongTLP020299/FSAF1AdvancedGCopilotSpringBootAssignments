package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.repository.ReviewRepository;
import com.fsoft.ecommerce.service.ProductRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductRatingServiceImpl implements ProductRatingService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Override
    public void updateProductAverageRating(Long productId) {
        // 1. Fetch product entity
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // 2. Calculate average rating from all reviews for this product using JPQL
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId)
            .orElse(0.0);
        
        // 3. Update product's average rating
        product.setAverageRating(averageRating);
        productRepository.save(product);
    }
}
