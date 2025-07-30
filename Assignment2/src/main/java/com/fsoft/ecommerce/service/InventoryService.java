package com.fsoft.ecommerce.service;

import com.fsoft.ecommerce.entity.Product;

/**
 * Service interface for managing product inventory operations
 */
public interface InventoryService {
    
    /**
     * Checks if sufficient stock is available for a product
     * 
     * @param productId the product ID to check
     * @param quantity the required quantity
     * @return true if sufficient stock is available
     */
    boolean isStockAvailable(Long productId, Integer quantity);
    
    /**
     * Reserves inventory for an order
     * 
     * @param productId the product ID
     * @param quantity the quantity to reserve
     * @return reservation details
     * @throws InsufficientStockException if insufficient stock
     */
    InventoryReservation reserveStock(Long productId, Integer quantity);
    
    /**
     * Commits a stock reservation
     * 
     * @param reservation the reservation to commit
     */
    void commitReservation(InventoryReservation reservation);
    
    /**
     * Rollbacks a stock reservation
     * 
     * @param reservation the reservation to rollback
     */
    void rollbackReservation(InventoryReservation reservation);
    
    /**
     * Represents an inventory reservation
     */
    class InventoryReservation {
        private final Product product;
        private final Integer quantity;
        private final String reservationId;
        
        public InventoryReservation(Product product, Integer quantity, String reservationId) {
            this.product = product;
            this.quantity = quantity;
            this.reservationId = reservationId;
        }
        
        // Getters
        public Product getProduct() { return product; }
        public Integer getQuantity() { return quantity; }
        public String getReservationId() { return reservationId; }
    }
}
