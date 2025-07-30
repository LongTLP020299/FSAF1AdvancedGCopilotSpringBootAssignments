package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.exception.InsufficientStockException;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of InventoryService for managing product inventory operations
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public boolean isStockAvailable(Long productId, Integer quantity) {
        return productRepository.findById(productId)
            .map(product -> product.getStockQuantity() >= quantity)
            .orElse(false);
    }

    @Override
    @Transactional
    public InventoryReservation reserveStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // Validate stock availability
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                String.format("Insufficient stock for product: %s. Available: %d, Requested: %d", 
                    product.getName(), product.getStockQuantity(), quantity)
            );
        }
        
        // Reserve stock
        product.setStockQuantity(product.getStockQuantity() - quantity);
        Product updatedProduct = productRepository.save(product);
        
        // Create reservation with unique ID
        String reservationId = UUID.randomUUID().toString();
        return new InventoryReservation(updatedProduct, quantity, reservationId);
    }

    @Override
    public void commitReservation(InventoryReservation reservation) {
        // In more complex systems, this would mark reservation as committed
        // For now, stock has already been updated during reservation
        logInventoryOperation("COMMITTED", reservation);
    }

    @Override
    @Transactional
    public void rollbackReservation(InventoryReservation reservation) {
        try {
            Product product = reservation.getProduct();
            // Restore stock
            product.setStockQuantity(product.getStockQuantity() + reservation.getQuantity());
            productRepository.save(product);
            
            logInventoryOperation("ROLLED_BACK", reservation);
        } catch (Exception e) {
            // Log error but don't throw to avoid masking original exception
            System.err.println("Failed to rollback inventory reservation: " + e.getMessage());
        }
    }

    /**
     * Logs inventory operations for audit trail
     */
    private void logInventoryOperation(String operation, InventoryReservation reservation) {
        System.out.println("Inventory " + operation + " for product " + 
            reservation.getProduct().getId() + " quantity " + reservation.getQuantity());
    }
}
