package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.entity.OrderStatus;
import com.fsoft.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseVerificationServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private PurchaseVerificationServiceImpl purchaseVerificationService;
    
    @Test
    void hasUserPurchasedProduct_UserHasPurchasedAndDelivered_ShouldReturnTrue() {
        // Arrange
        Long userId = 1L;
        Long productId = 1L;
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(true);
        
        // Act
        boolean result = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertTrue(result);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    @Test
    void hasUserPurchasedProduct_UserHasNotPurchased_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        Long productId = 1L;
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(false);
        
        // Act
        boolean result = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertFalse(result);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    @Test
    void hasUserPurchasedProduct_UserPurchasedButNotDelivered_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        Long productId = 1L;
        // Repository returns false because order exists but status is not DELIVERED
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(false);
        
        // Act
        boolean result = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertFalse(result);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    @Test
    void hasUserPurchasedProduct_DifferentUser_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        Long productId = 1L;
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(false);
        
        // Act
        boolean result = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertFalse(result);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    @Test
    void hasUserPurchasedProduct_DifferentProduct_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        Long productId = 1L;
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(false);
        
        // Act
        boolean result = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertFalse(result);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    @Test
    void hasUserPurchasedProduct_WithNullUserId_ShouldStillCallRepository() {
        // Arrange
        Long userId = null;
        Long productId = 1L;
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(false);
        
        // Act
        boolean result = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertFalse(result);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    @Test
    void hasUserPurchasedProduct_WithNullProductId_ShouldStillCallRepository() {
        // Arrange
        Long userId = 1L;
        Long productId = null;
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(false);
        
        // Act
        boolean result = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertFalse(result);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    @Test
    void hasUserPurchasedProduct_MultipleCallsSameParameters_ShouldCallRepositoryEachTime() {
        // Arrange
        Long userId = 1L;
        Long productId = 1L;
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(true);
        
        // Act
        boolean result1 = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        boolean result2 = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(orderRepository, times(2)).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    @Test
    void hasUserPurchasedProduct_OnlyChecksDELIVEREDStatus_ShouldNotCheckOtherStatuses() {
        // Arrange
        Long userId = 1L;
        Long productId = 1L;
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED)).thenReturn(true);
        
        // Act
        boolean result = purchaseVerificationService.hasUserPurchasedProduct(userId, productId);
        
        // Assert
        assertTrue(result);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
        
        // Verify that only DELIVERED status is checked, not other statuses
        verify(orderRepository, never()).existsByUserIdAndOrderItemsProductIdAndStatus(
            eq(userId), eq(productId), eq(OrderStatus.PENDING));
        verify(orderRepository, never()).existsByUserIdAndOrderItemsProductIdAndStatus(
            eq(userId), eq(productId), eq(OrderStatus.CONFIRMED));
        verify(orderRepository, never()).existsByUserIdAndOrderItemsProductIdAndStatus(
            eq(userId), eq(productId), eq(OrderStatus.SHIPPED));
        verify(orderRepository, never()).existsByUserIdAndOrderItemsProductIdAndStatus(
            eq(userId), eq(productId), eq(OrderStatus.CANCELLED));
    }
}
