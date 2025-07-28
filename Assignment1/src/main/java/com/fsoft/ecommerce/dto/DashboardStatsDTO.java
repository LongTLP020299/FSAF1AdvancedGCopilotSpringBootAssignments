package com.fsoft.ecommerce.dto;

import java.math.BigDecimal;

public class DashboardStatsDTO {
    
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long newCustomersThisMonth;
    
    // Constructors
    public DashboardStatsDTO() {}
    
    public DashboardStatsDTO(BigDecimal totalRevenue, Long totalOrders, Long newCustomersThisMonth) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.newCustomersThisMonth = newCustomersThisMonth;
    }
    
    // Getters and Setters
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public Long getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public Long getNewCustomersThisMonth() {
        return newCustomersThisMonth;
    }
    
    public void setNewCustomersThisMonth(Long newCustomersThisMonth) {
        this.newCustomersThisMonth = newCustomersThisMonth;
    }
}
