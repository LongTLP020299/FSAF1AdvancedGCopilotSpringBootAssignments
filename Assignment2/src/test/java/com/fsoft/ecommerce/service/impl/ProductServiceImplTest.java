package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.ProductDTO;
import com.fsoft.ecommerce.entity.Product;
import com.fsoft.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private Product testProduct;
    private ProductDTO testProductDTO;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(10);
        testProduct.setCategory("Electronics");
        testProduct.setImageUrl("http://example.com/image.jpg");
        testProduct.setAverageRating(4.5);
        testProduct.setCreatedAt(LocalDateTime.now());
        
        testProductDTO = new ProductDTO();
        testProductDTO.setId(1L);
        testProductDTO.setName("Test Product");
        testProductDTO.setDescription("Test Description");
        testProductDTO.setPrice(new BigDecimal("99.99"));
        testProductDTO.setStockQuantity(10);
        testProductDTO.setCategory("Electronics");
        testProductDTO.setImageUrl("http://example.com/image.jpg");
        testProductDTO.setAverageRating(4.5);
        testProductDTO.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);
        
        when(productRepository.findAll(pageable)).thenReturn(productPage);
        
        // Act
        Page<ProductDTO> result = productService.getAllProducts(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
        verify(productRepository).findAll(pageable);
    }
    
    @Test
    void getProductById_ExistingProduct_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        Optional<ProductDTO> result = productService.getProductById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
        assertEquals(new BigDecimal("99.99"), result.get().getPrice());
        verify(productRepository).findById(1L);
    }
    
    @Test
    void getProductById_NonExistingProduct_ShouldReturnEmpty() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        Optional<ProductDTO> result = productService.getProductById(1L);
        
        // Assert
        assertFalse(result.isPresent());
        verify(productRepository).findById(1L);
    }
    
    @Test
    void createProduct_ShouldSetCreatedAtAndAverageRating() {
        // Arrange
        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Product");
        savedProduct.setCreatedAt(LocalDateTime.now());
        savedProduct.setAverageRating(0.0);
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        
        // Act
        ProductDTO result = productService.createProduct(testProductDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).save(any(Product.class));
        
        // Verify that created product has proper initial values
        verify(productRepository).save(argThat(product -> 
            product.getCreatedAt() != null && 
            product.getAverageRating().equals(0.0)
        ));
    }
    
    @Test
    void updateProduct_ExistingProduct_ShouldReturnUpdatedProduct() {
        // Arrange
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setPrice(new BigDecimal("149.99"));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        Optional<ProductDTO> result = productService.updateProduct(1L, updateDTO);
        
        // Assert
        assertTrue(result.isPresent());
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }
    
    @Test
    void updateProduct_NonExistingProduct_ShouldReturnEmpty() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        Optional<ProductDTO> result = productService.updateProduct(1L, testProductDTO);
        
        // Assert
        assertFalse(result.isPresent());
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void updateProduct_PartialUpdate_ShouldOnlyUpdateProvidedFields() {
        // Arrange
        ProductDTO partialUpdateDTO = new ProductDTO();
        partialUpdateDTO.setName("Updated Name");
        partialUpdateDTO.setPrice(new BigDecimal("199.99"));
        // Other fields are null, should not be updated
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        Optional<ProductDTO> result = productService.updateProduct(1L, partialUpdateDTO);
        
        // Assert
        assertTrue(result.isPresent());
        verify(productRepository).save(argThat(product -> 
            "Updated Name".equals(product.getName()) &&
            new BigDecimal("199.99").equals(product.getPrice()) &&
            "Test Description".equals(product.getDescription()) // Should remain unchanged
        ));
    }
    
    @Test
    void deleteProduct_ShouldCallRepository() {
        // Act
        productService.deleteProduct(1L);
        
        // Assert
        verify(productRepository).deleteById(1L);
    }
    
    @Test
    void getProductsByCategory_ShouldReturnFilteredProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);
        
        when(productRepository.findByCategoryOptimized("Electronics", pageable)).thenReturn(productPage);
        
        // Act
        Page<ProductDTO> result = productService.getProductsByCategory("Electronics", pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Electronics", result.getContent().get(0).getCategory());
        verify(productRepository).findByCategoryOptimized("Electronics", pageable);
    }
    
    @Test
    void toDTO_ShouldMapAllFields() {
        // Act
        Optional<ProductDTO> result = productService.getProductById(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        result = productService.getProductById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        ProductDTO dto = result.get();
        assertEquals(testProduct.getId(), dto.getId());
        assertEquals(testProduct.getName(), dto.getName());
        assertEquals(testProduct.getDescription(), dto.getDescription());
        assertEquals(testProduct.getPrice(), dto.getPrice());
        assertEquals(testProduct.getStockQuantity(), dto.getStockQuantity());
        assertEquals(testProduct.getCategory(), dto.getCategory());
        assertEquals(testProduct.getImageUrl(), dto.getImageUrl());
        assertEquals(testProduct.getAverageRating(), dto.getAverageRating());
        assertEquals(testProduct.getCreatedAt(), dto.getCreatedAt());
    }
    
    @Test
    void updateEntityFromDTO_WithNullFields_ShouldNotUpdateNullFields() {
        // Arrange
        ProductDTO dtoWithNulls = new ProductDTO();
        dtoWithNulls.setName("New Name");
        // All other fields are null
        
        String originalDescription = testProduct.getDescription();
        BigDecimal originalPrice = testProduct.getPrice();
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Act
        productService.updateProduct(1L, dtoWithNulls);
        
        // Assert
        verify(productRepository).save(argThat(product -> 
            "New Name".equals(product.getName()) &&
            originalDescription.equals(product.getDescription()) &&
            originalPrice.equals(product.getPrice())
        ));
    }
}
