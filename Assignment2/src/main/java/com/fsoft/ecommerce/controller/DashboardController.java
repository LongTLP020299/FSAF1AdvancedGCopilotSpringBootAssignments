package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.dto.DashboardStatsDTO;
import com.fsoft.ecommerce.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can access financial data
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO dashboardStats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(dashboardStats);
    }
}
