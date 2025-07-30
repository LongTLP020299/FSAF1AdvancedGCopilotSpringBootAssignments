package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.CategoryDTO;
import com.fsoft.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private CategoryServiceImpl categoryService;
    
    private CategoryDTO testCategoryDTO;
    private List<String> testCategories;
    
    @BeforeEach
    void setUp() {
        testCategoryDTO = new CategoryDTO();
        testCategoryDTO.setId(1L);
        testCategoryDTO.setName("Electronics");
        
        testCategories = Arrays.asList("Electronics", "Books", "Clothing", "Sports");
    }
    
    @Test
    void getAllCategories_ShouldReturnPageOfCategories() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        when(productRepository.findAllCategories()).thenReturn(testCategories);
        
        // Act
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        assertEquals("Electronics", result.getContent().get(0).getName());
        assertEquals("Books", result.getContent().get(1).getName());
        verify(productRepository).findAllCategories();
    }
    
    @Test
    void getAllCategories_WithSecondPage_ShouldReturnCorrectSubset() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 2); // Second page, 2 items per page
        when(productRepository.findAllCategories()).thenReturn(testCategories);
        
        // Act
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        assertEquals("Clothing", result.getContent().get(0).getName());
        assertEquals("Sports", result.getContent().get(1).getName());
        verify(productRepository).findAllCategories();
    }
    
    @Test
    void getAllCategories_WithOffsetBeyondSize_ShouldReturnEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(10, 2); // Way beyond available data
        when(productRepository.findAllCategories()).thenReturn(testCategories);
        
        // Act
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        verify(productRepository).findAllCategories();
    }
    
    @Test
    void getAllCategories_WithEmptyCategories_ShouldReturnEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAllCategories()).thenReturn(List.of());
        
        // Act
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        verify(productRepository).findAllCategories();
    }
    
    @Test
    void getCategoryById_ShouldReturnEmpty() {
        // Act
        Optional<CategoryDTO> result = categoryService.getCategoryById(1L);
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void createCategory_ShouldReturnSameCategory() {
        // Act
        CategoryDTO result = categoryService.createCategory(testCategoryDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(testCategoryDTO.getId(), result.getId());
        assertEquals(testCategoryDTO.getName(), result.getName());
    }
    
    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        // Act
        Optional<CategoryDTO> result = categoryService.updateCategory(1L, testCategoryDTO);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCategoryDTO.getId(), result.get().getId());
        assertEquals(testCategoryDTO.getName(), result.get().getName());
    }
    
    @Test
    void deleteCategory_ShouldCompleteWithoutError() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
    }
    
    @Test
    void getAllCategoryNames_ShouldReturnListOfNames() {
        // Arrange
        when(productRepository.findAllCategories()).thenReturn(testCategories);
        
        // Act
        List<String> result = categoryService.getAllCategoryNames();
        
        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("Electronics", result.get(0));
        assertEquals("Books", result.get(1));
        assertEquals("Clothing", result.get(2));
        assertEquals("Sports", result.get(3));
        verify(productRepository).findAllCategories();
    }
    
    @Test
    void getAllCategoryNames_WithEmptyRepository_ShouldReturnEmptyList() {
        // Arrange
        when(productRepository.findAllCategories()).thenReturn(List.of());
        
        // Act
        List<String> result = categoryService.getAllCategoryNames();
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository).findAllCategories();
    }
    
    @Test
    void getAllCategories_WithPartialLastPage_ShouldReturnCorrectSubset() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 3); // Second page, 3 items per page
        when(productRepository.findAllCategories()).thenReturn(testCategories);
        
        // Act
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size()); // Only 1 item on last page
        assertEquals(4, result.getTotalElements());
        assertEquals("Sports", result.getContent().get(0).getName());
        verify(productRepository).findAllCategories();
    }
}
