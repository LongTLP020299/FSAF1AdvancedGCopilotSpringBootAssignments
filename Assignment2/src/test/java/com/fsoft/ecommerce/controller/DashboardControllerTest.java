package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.config.SecurityConfig;
import com.fsoft.ecommerce.dto.DashboardStatsDTO;
import com.fsoft.ecommerce.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@ContextConfiguration(classes = {DashboardController.class, SecurityConfig.class})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    private DashboardStatsDTO testStats;

    @BeforeEach
    void setUp() {
        testStats = new DashboardStatsDTO(
            new BigDecimal("50000.00"),
            150L,
            25L
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardStats_WithAdminRole_ShouldReturnStats() throws Exception {
        // Arrange
        when(dashboardService.getDashboardStats()).thenReturn(testStats);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(50000.00))
                .andExpect(jsonPath("$.totalOrders").value(150))
                .andExpect(jsonPath("$.newCustomersThisMonth").value(25));

        verify(dashboardService).getDashboardStats();
    }

    @Test
    @WithMockUser(roles = "USER") // Wrong role
    void getDashboardStats_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(dashboardService);
    }

    @Test
    void getDashboardStats_WithoutAuthentication_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(dashboardService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardStats_WithZeroValues_ShouldReturnZeroStats() throws Exception {
        // Arrange
        DashboardStatsDTO zeroStats = new DashboardStatsDTO(
            BigDecimal.ZERO,
            0L,
            0L
        );
        when(dashboardService.getDashboardStats()).thenReturn(zeroStats);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0))
                .andExpect(jsonPath("$.totalOrders").value(0))
                .andExpect(jsonPath("$.newCustomersThisMonth").value(0));

        verify(dashboardService).getDashboardStats();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardStats_WithLargeValues_ShouldReturnCorrectly() throws Exception {
        // Arrange
        DashboardStatsDTO largeStats = new DashboardStatsDTO(
            new BigDecimal("999999999.99"),
            Long.MAX_VALUE,
            1000000L
        );
        when(dashboardService.getDashboardStats()).thenReturn(largeStats);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(999999999.99))
                .andExpect(jsonPath("$.totalOrders").value(Long.MAX_VALUE))
                .andExpect(jsonPath("$.newCustomersThisMonth").value(1000000));

        verify(dashboardService).getDashboardStats();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"}) // Multiple roles, ADMIN should take precedence
    void getDashboardStats_WithMultipleRoles_ShouldAllowAccess() throws Exception {
        // Arrange
        when(dashboardService.getDashboardStats()).thenReturn(testStats);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(50000.00));

        verify(dashboardService).getDashboardStats();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getDashboardStats_WithSpecificAdminUser_ShouldReturnStats() throws Exception {
        // Arrange
        when(dashboardService.getDashboardStats()).thenReturn(testStats);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(50000.00))
                .andExpect(jsonPath("$.totalOrders").value(150))
                .andExpect(jsonPath("$.newCustomersThisMonth").value(25));

        verify(dashboardService).getDashboardStats();
    }

    /**
     * Test that demonstrates Spring Security role-based access control.
     * A regular USER attempting to access admin-only dashboard endpoint
     * should receive a 403 Forbidden response due to insufficient privileges.
     * 
     * This test validates:
     * - @PreAuthorize("hasRole('ADMIN')") annotation on the controller method
     * - Spring Security's method-level security is properly configured
     * - Authorization filter correctly denies access for non-admin users
     */
    @Test
    @WithMockUser(username = "regularUser", roles = "USER")
    void regularUser_AccessingDashboard_ShouldReceiveForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"));

        // Verify that the service method is never called due to authorization failure
        verifyNoInteractions(dashboardService);
    }
}
