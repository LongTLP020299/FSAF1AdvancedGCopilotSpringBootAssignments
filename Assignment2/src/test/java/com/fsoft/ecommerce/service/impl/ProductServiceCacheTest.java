package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.exception.ProductNotFoundException;
import com.fsoft.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test class for ProductServiceImpl cache functionality
 */
@ExtendWith(MockitoExtension.class)
public class ProductServiceCacheTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(10);
        testProduct.setCategory("Electronics");
    }

    @Test
    void testFindProductByIdExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.findProductById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getDescription(), result.getDescription());
        assertEquals(testProduct.getPrice(), result.getPrice());
        
        // Verify repository was called
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindProductByIdNotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            productService.findProductById(999L);
        });
        
        verify(productRepository, times(1)).findById(999L);
    }

    @Test 
    void testUpdateProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        var productDTO = new com.fsoft.ecommerce.dto.ProductDTO();
        productDTO.setName("Updated Product");
        productDTO.setDescription("Updated Description");
        productDTO.setPrice(new BigDecimal("199.99"));
        productDTO.setStockQuantity(20);
        productDTO.setCategory("Electronics");
        
        var result = productService.updateProduct(1L, productDTO);
        
        // Then
        assertTrue(result.isPresent());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        var productDTO = new com.fsoft.ecommerce.dto.ProductDTO();
        productDTO.setName("Updated Product");
        
        var result = productService.updateProduct(999L, productDTO);
        
        // Then
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }
}
