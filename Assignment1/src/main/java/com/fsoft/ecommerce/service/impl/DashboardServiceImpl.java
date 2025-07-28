package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.dto.DashboardStatsDTO;
import com.fsoft.ecommerce.repository.OrderRepository;
import com.fsoft.ecommerce.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public DashboardStatsDTO getDashboardStats() {
        Map<String, Object> stats = orderRepository.getDashboardStats();
        
        // Extract and handle null values safely with helper method
        BigDecimal totalRevenue = extractBigDecimal(stats, "totalRevenue");
        Long totalOrders = extractLong(stats, "totalOrders");
        Long newCustomersThisMonth = extractLong(stats, "newCustomersThisMonth");
        
        return new DashboardStatsDTO(totalRevenue, totalOrders, newCustomersThisMonth);
    }
    
    /**
     * Helper method to safely extract BigDecimal from Map with null handling
     */
    private BigDecimal extractBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? (BigDecimal) value : BigDecimal.ZERO;
    }
    
    /**
     * Helper method to safely extract Long from Map with null handling
     */
    private Long extractLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? ((Number) value).longValue() : 0L;
    }
}
