package com.fsoft.ecommerce.service;

import com.fsoft.ecommerce.dto.CreateOrderRequestDTO;
import com.fsoft.ecommerce.entity.Order;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.entity.User;

/**
 * Service interface for building order entities
 */
public interface OrderBuilderService {
    
    /**
     * Builds a complete order entity from request and validated data
     * 
     * @param request the order request
     * @param user the validated user
     * @param product the validated product
     * @return the complete order entity
     */
    Order buildOrder(CreateOrderRequestDTO request, User user, Product product);
    
    /**
     * Calculates the total amount for an order
     * 
     * @param product the product being ordered
     * @param quantity the quantity
     * @return the calculated total amount
     */
    java.math.BigDecimal calculateOrderTotal(Product product, Integer quantity);
    
    /**
     * Creates order items for a product
     * 
     * @param order the parent order
     * @param product the product
     * @param quantity the quantity
     * @return set of order items
     */
    java.util.Set<com.fsoft.ecommerce.entity.OrderItem> createOrderItems(
            Order order, Product product, Integer quantity);
}
