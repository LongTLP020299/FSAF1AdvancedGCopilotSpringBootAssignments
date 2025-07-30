package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.DashboardStatsDTO;
import com.fsoft.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private DashboardServiceImpl dashboardService;
    
    private Map<String, Object> testStats;
    
    @BeforeEach
    void setUp() {
        testStats = new HashMap<>();
        testStats.put("totalRevenue", new BigDecimal("50000.00"));
        testStats.put("totalOrders", 150L);
        testStats.put("newCustomersThisMonth", 25L);
    }
    
    @Test
    void getDashboardStats_WithValidData_ShouldReturnCorrectStats() {
        // Arrange
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("50000.00"), result.getTotalRevenue());
        assertEquals(150L, result.getTotalOrders());
        assertEquals(25L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithNullTotalRevenue_ShouldReturnZero() {
        // Arrange
        testStats.put("totalRevenue", null);
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(150L, result.getTotalOrders());
        assertEquals(25L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithNullTotalOrders_ShouldReturnZero() {
        // Arrange
        testStats.put("totalOrders", null);
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("50000.00"), result.getTotalRevenue());
        assertEquals(0L, result.getTotalOrders());
        assertEquals(25L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithNullNewCustomers_ShouldReturnZero() {
        // Arrange
        testStats.put("newCustomersThisMonth", null);
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("50000.00"), result.getTotalRevenue());
        assertEquals(150L, result.getTotalOrders());
        assertEquals(0L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithAllNullValues_ShouldReturnZeros() {
        // Arrange
        testStats.put("totalRevenue", null);
        testStats.put("totalOrders", null);
        testStats.put("newCustomersThisMonth", null);
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(0L, result.getTotalOrders());
        assertEquals(0L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithEmptyMap_ShouldReturnZeros() {
        // Arrange
        Map<String, Object> emptyStats = new HashMap<>();
        when(orderRepository.getDashboardStats()).thenReturn(emptyStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(0L, result.getTotalOrders());
        assertEquals(0L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithIntegerValues_ShouldConvertCorrectly() {
        // Arrange
        testStats.put("totalOrders", 150); // Integer instead of Long
        testStats.put("newCustomersThisMonth", 25); // Integer instead of Long
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("50000.00"), result.getTotalRevenue());
        assertEquals(150L, result.getTotalOrders());
        assertEquals(25L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithZeroValues_ShouldReturnZeros() {
        // Arrange
        testStats.put("totalRevenue", BigDecimal.ZERO);
        testStats.put("totalOrders", 0L);
        testStats.put("newCustomersThisMonth", 0L);
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(0L, result.getTotalOrders());
        assertEquals(0L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithLargeValues_ShouldHandleCorrectly() {
        // Arrange
        testStats.put("totalRevenue", new BigDecimal("999999999.99"));
        testStats.put("totalOrders", Long.MAX_VALUE);
        testStats.put("newCustomersThisMonth", 1000000L);
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("999999999.99"), result.getTotalRevenue());
        assertEquals(Long.MAX_VALUE, result.getTotalOrders());
        assertEquals(1000000L, result.getNewCustomersThisMonth());
        verify(orderRepository).getDashboardStats();
    }
    
    @Test
    void getDashboardStats_WithDoubleValues_ShouldConvertCorrectly() {
        // Arrange
        testStats.put("totalOrders", 150.0); // Double instead of Long
        testStats.put("newCustomersThisMonth", 25.5); // Double that will be converted to Long
        when(orderRepository.getDashboardStats()).thenReturn(testStats);
        
        // Act
        DashboardStatsDTO result = dashboardService.getDashboardStats();
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("50000.00"), result.getTotalRevenue());
        assertEquals(150L, result.getTotalOrders());
        assertEquals(25L, result.getNewCustomersThisMonth()); // Should truncate to 25
        verify(orderRepository).getDashboardStats();
    }
}
