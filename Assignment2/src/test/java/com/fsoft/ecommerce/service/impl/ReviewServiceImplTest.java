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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private PurchaseVerificationService purchaseVerificationService;
    
    @Mock
    private ProductRatingService productRatingService;
    
    @InjectMocks
    private ReviewServiceImpl reviewService;
    
    private ReviewDTO testReviewDTO;
    private User testUser;
    private Product testProduct;
    private Review testReview;
    
    @BeforeEach
    void setUp() {
        testReviewDTO = new ReviewDTO();
        testReviewDTO.setRating(5);
        testReviewDTO.setComment("Great product!");
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        
        testReview = new Review();
        testReview.setId(1L);
        testReview.setUser(testUser);
        testReview.setProduct(testProduct);
        testReview.setRating(5);
        testReview.setComment("Great product!");
    }
    
    @Test
    void addReview_ValidInput_ShouldCreateReview() {
        // Arrange
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        
        // Act
        Review result = reviewService.addReview(testReviewDTO, 1L, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great product!", result.getComment());
        verify(purchaseVerificationService).hasUserPurchasedProduct(1L, 1L);
        verify(reviewRepository).existsByUserIdAndProductId(1L, 1L);
        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(reviewRepository).save(any(Review.class));
        verify(productRatingService).updateProductAverageRating(1L);
    }
    
    @Test
    void addReview_InvalidRatingTooLow_ShouldThrowException() {
        // Arrange
        testReviewDTO.setRating(0);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.addReview(testReviewDTO, 1L, 1L)
        );
        
        assertEquals("Rating must be between 1 and 5", exception.getMessage());
        verifyNoInteractions(purchaseVerificationService);
    }
    
    @Test
    void addReview_InvalidRatingTooHigh_ShouldThrowException() {
        // Arrange
        testReviewDTO.setRating(6);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.addReview(testReviewDTO, 1L, 1L)
        );
        
        assertEquals("Rating must be between 1 and 5", exception.getMessage());
        verifyNoInteractions(purchaseVerificationService);
    }
    
    @Test
    void addReview_UserNotPurchasedProduct_ShouldThrowException() {
        // Arrange
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(false);
        
        // Act & Assert
        UserNotPurchasedProductException exception = assertThrows(
            UserNotPurchasedProductException.class,
            () -> reviewService.addReview(testReviewDTO, 1L, 1L)
        );
        
        assertEquals("User must purchase the product before reviewing it", exception.getMessage());
        verify(purchaseVerificationService).hasUserPurchasedProduct(1L, 1L);
        verifyNoInteractions(reviewRepository);
    }
    
    @Test
    void addReview_DuplicateReview_ShouldThrowException() {
        // Arrange
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);
        
        // Act & Assert
        DuplicateReviewException exception = assertThrows(
            DuplicateReviewException.class,
            () -> reviewService.addReview(testReviewDTO, 1L, 1L)
        );
        
        assertEquals("User has already reviewed this product", exception.getMessage());
        verify(reviewRepository).existsByUserIdAndProductId(1L, 1L);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(productRepository);
    }
    
    @Test
    void addReview_UserNotFound_ShouldThrowException() {
        // Arrange
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> reviewService.addReview(testReviewDTO, 1L, 1L)
        );
        
        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verifyNoInteractions(productRepository);
    }
    
    @Test
    void addReview_ProductNotFound_ShouldThrowException() {
        // Arrange
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> reviewService.addReview(testReviewDTO, 1L, 1L)
        );
        
        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository).findById(1L);
        verifyNoInteractions(productRatingService);
    }
    
    @Test
    void getReviewsByProduct_ExistingProduct_ShouldReturnReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(testReview);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findByProductIdOrderByCreatedAtDesc(1L)).thenReturn(reviews);
        
        // Act
        List<Review> result = reviewService.getReviewsByProduct(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Great product!", result.get(0).getComment());
        verify(productRepository).existsById(1L);
        verify(reviewRepository).findByProductIdOrderByCreatedAtDesc(1L);
    }
    
    @Test
    void getReviewsByProduct_NonExistingProduct_ShouldThrowException() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);
        
        // Act & Assert
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> reviewService.getReviewsByProduct(1L)
        );
        
        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository).existsById(1L);
        verifyNoInteractions(reviewRepository);
    }
    
    @Test
    void getReviewsByUser_ShouldReturnUserReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByUserId(1L)).thenReturn(reviews);
        
        // Act
        List<Review> result = reviewService.getReviewsByUser(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Great product!", result.get(0).getComment());
        verify(reviewRepository).findByUserId(1L);
    }
    
    @Test
    void updateProductAverageRating_ShouldDelegateToRatingService() {
        // Act
        reviewService.updateProductAverageRating(1L);
        
        // Assert
        verify(productRatingService).updateProductAverageRating(1L);
    }
    
    @Test
    void addReview_ValidBoundaryRating1_ShouldCreateReview() {
        // Arrange
        testReviewDTO.setRating(1);
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        
        // Act
        Review result = reviewService.addReview(testReviewDTO, 1L, 1L);
        
        // Assert
        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
    }
    
    @Test
    void addReview_ValidBoundaryRating5_ShouldCreateReview() {
        // Arrange
        testReviewDTO.setRating(5);
        when(purchaseVerificationService.hasUserPurchasedProduct(1L, 1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        
        // Act
        Review result = reviewService.addReview(testReviewDTO, 1L, 1L);
        
        // Assert
        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
    }
}
