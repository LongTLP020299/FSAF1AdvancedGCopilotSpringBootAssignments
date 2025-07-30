package com.fsoft.ecommerce.service;

import com.fsoft.ecommerce.dto.CreateOrderRequestDTO;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.entity.User;

/**
 * Service interface for validating order operations
 */
public interface OrderValidationService {
    
    /**
     * Validates an order request and returns validation result
     * 
     * @param request the order request to validate
     * @return validation result containing validated entities
     * @throws IllegalArgumentException if request is invalid
     * @throws UserNotFoundException if user is not found
     * @throws ProductNotFoundException if product is not found
     */
    OrderValidationResult validateOrderRequest(CreateOrderRequestDTO request);
    
    /**
     * Validates if a user can place an order for a specific product
     * 
     * @param userId the user ID
     * @param productId the product ID
     * @param quantity the quantity
     * @return true if order can be placed
     */
    boolean canUserPlaceOrder(Long userId, Long productId, Integer quantity);
    
    /**
     * Result of order validation containing validated entities
     */
    class OrderValidationResult {
        private final User user;
        private final Product product;
        private final boolean valid;
        private final String errorMessage;
        
        public OrderValidationResult(User user, Product product) {
            this.user = user;
            this.product = product;
            this.valid = true;
            this.errorMessage = null;
        }
        
        public OrderValidationResult(String errorMessage) {
            this.user = null;
            this.product = null;
            this.valid = false;
            this.errorMessage = errorMessage;
        }
        
        // Getters
        public User getUser() { return user; }
        public Product getProduct() { return product; }
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
}
