package com.fsoft.ecommerce.config;

import com.fsoft.ecommerce.interceptor.RateLimitingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for registering interceptors, handlers, and other web-related components.
 * 
 * @author E-commerce Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitingInterceptor rateLimitingInterceptor;
    
    /**
     * Register interceptors for the application.
     * Currently registers rate limiting interceptor for login endpoint.
     * 
     * @param registry Interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/api/auth/login"); // Only apply to login endpoint
    }
}
