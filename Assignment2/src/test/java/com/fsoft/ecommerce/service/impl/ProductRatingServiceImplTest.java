package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.repository.ProductRepository;
import com.fsoft.ecommerce.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRatingServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ReviewRepository reviewRepository;
    
    @InjectMocks
    private ProductRatingServiceImpl productRatingService;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setAverageRating(0.0);
    }
    
    @Test
    void updateProductAverageRating_ExistingProductWithReviews_ShouldUpdateRating() {
        // Arrange
        Double averageRating = 4.5;
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.of(averageRating));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        productRatingService.updateProductAverageRating(1L);
        
        // Assert
        verify(productRepository).findById(1L);
        verify(reviewRepository).getAverageRatingByProductId(1L);
        verify(productRepository).save(argThat(product -> 
            product.getAverageRating().equals(4.5)
        ));
    }
    
    @Test
    void updateProductAverageRating_ExistingProductWithoutReviews_ShouldSetRatingToZero() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        productRatingService.updateProductAverageRating(1L);
        
        // Assert
        verify(productRepository).findById(1L);
        verify(reviewRepository).getAverageRatingByProductId(1L);
        verify(productRepository).save(argThat(product -> 
            product.getAverageRating().equals(0.0)
        ));
    }
    
    @Test
    void updateProductAverageRating_NonExistingProduct_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> productRatingService.updateProductAverageRating(1L)
        );
        
        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository).findById(1L);
        verifyNoInteractions(reviewRepository);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void updateProductAverageRating_WithPerfectRating_ShouldUpdateCorrectly() {
        // Arrange
        Double perfectRating = 5.0;
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.of(perfectRating));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        productRatingService.updateProductAverageRating(1L);
        
        // Assert
        verify(productRepository).save(argThat(product -> 
            product.getAverageRating().equals(5.0)
        ));
    }
    
    @Test
    void updateProductAverageRating_WithMinimumRating_ShouldUpdateCorrectly() {
        // Arrange
        Double minimumRating = 1.0;
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.of(minimumRating));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        productRatingService.updateProductAverageRating(1L);
        
        // Assert
        verify(productRepository).save(argThat(product -> 
            product.getAverageRating().equals(1.0)
        ));
    }
    
    @Test
    void updateProductAverageRating_WithDecimalRating_ShouldUpdateCorrectly() {
        // Arrange
        Double decimalRating = 3.75;
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.of(decimalRating));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        productRatingService.updateProductAverageRating(1L);
        
        // Assert
        verify(productRepository).save(argThat(product -> 
            product.getAverageRating().equals(3.75)
        ));
    }
    
    @Test
    void updateProductAverageRating_ProductAlreadyHasRating_ShouldOverwriteWithNewRating() {
        // Arrange
        testProduct.setAverageRating(2.5); // Existing rating
        Double newRating = 4.0;
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(Optional.of(newRating));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        productRatingService.updateProductAverageRating(1L);
        
        // Assert
        verify(productRepository).save(argThat(product -> 
            product.getAverageRating().equals(4.0) && 
            !product.getAverageRating().equals(2.5)
        ));
    }
}
