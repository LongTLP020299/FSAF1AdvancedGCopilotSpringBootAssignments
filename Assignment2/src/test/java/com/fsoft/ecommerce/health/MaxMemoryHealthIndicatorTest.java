package com.fsoft.ecommerce.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
public class MaxMemoryHealthIndicatorTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    @WithMockUser(roles = "USER")
    public void testMemoryHealthEndpoint() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        mockMvc.perform(get("/health/memory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details.maxMemory").exists())
                .andExpect(jsonPath("$.details.totalMemory").exists())
                .andExpect(jsonPath("$.details.usedMemory").exists())
                .andExpect(jsonPath("$.details.freeMemory").exists())
                .andExpect(jsonPath("$.details.memoryUsagePercentage").exists())
                .andExpect(jsonPath("$.details.threshold").value("90%"));
    }

    @Test  
    @WithMockUser(roles = "USER")
    public void testMemoryHealthResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        mockMvc.perform(get("/health/memory"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(org.hamcrest.Matchers.oneOf("UP", "DOWN")));
    }
}
