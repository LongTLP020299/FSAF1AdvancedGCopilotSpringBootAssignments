package com.fsoft.ecommerce.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    void testProductDefaultConstructor() {
        Product newProduct = new Product();
        assertNotNull(newProduct);
        assertEquals(0.0, newProduct.getAverageRating()); // Default rating
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        product.setId(id);
        assertEquals(id, product.getId());
    }

    @Test
    void testSetAndGetName() {
        String name = "Test Product";
        product.setName(name);
        assertEquals(name, product.getName());
    }

    @Test
    void testSetAndGetDescription() {
        String description = "This is a test product";
        product.setDescription(description);
        assertEquals(description, product.getDescription());
    }

    @Test
    void testSetAndGetPrice() {
        BigDecimal price = new BigDecimal("99.99");
        product.setPrice(price);
        assertEquals(price, product.getPrice());
    }

    @Test
    void testSetAndGetStockQuantity() {
        Integer stockQuantity = 100;
        product.setStockQuantity(stockQuantity);
        assertEquals(stockQuantity, product.getStockQuantity());
    }

    @Test
    void testSetAndGetCategory() {
        String category = "Electronics";
        product.setCategory(category);
        assertEquals(category, product.getCategory());
    }

    @Test
    void testSetAndGetImageUrl() {
        String imageUrl = "http://example.com/image.jpg";
        product.setImageUrl(imageUrl);
        assertEquals(imageUrl, product.getImageUrl());
    }

    @Test
    void testSetAndGetAverageRating() {
        Double averageRating = 4.5;
        product.setAverageRating(averageRating);
        assertEquals(averageRating, product.getAverageRating());
    }

    @Test
    void testSetAndGetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        product.setCreatedAt(createdAt);
        assertEquals(createdAt, product.getCreatedAt());
    }

    @Test
    void testSetAndGetReviews() {
        Review review1 = new Review();
        review1.setId(1L);
        Review review2 = new Review();
        review2.setId(2L);
        
        Set<Review> reviews = new HashSet<>();
        reviews.add(review1);
        reviews.add(review2);
        product.setReviews(reviews);
        
        assertEquals(reviews, product.getReviews());
        assertEquals(2, product.getReviews().size());
    }

    @Test
    void testSetAndGetOrderItems() {
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        
        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(item1);
        orderItems.add(item2);
        product.setOrderItems(orderItems);
        
        assertEquals(orderItems, product.getOrderItems());
        assertEquals(2, product.getOrderItems().size());
    }

    @Test
    void testProductWithAllFields() {
        Long id = 1L;
        String name = "Test Product";
        String description = "Description";
        BigDecimal price = new BigDecimal("149.99");
        Integer stockQuantity = 50;
        String category = "Books";
        String imageUrl = "http://example.com/book.jpg";
        Double averageRating = 3.8;
        LocalDateTime createdAt = LocalDateTime.now();

        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setAverageRating(averageRating);
        product.setCreatedAt(createdAt);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(stockQuantity, product.getStockQuantity());
        assertEquals(category, product.getCategory());
        assertEquals(imageUrl, product.getImageUrl());
        assertEquals(averageRating, product.getAverageRating());
        assertEquals(createdAt, product.getCreatedAt());
    }

    @Test
    void testProductWithNullValues() {
        product.setId(null);
        product.setName(null);
        product.setDescription(null);
        product.setPrice(null);
        product.setStockQuantity(null);
        product.setCategory(null);
        product.setImageUrl(null);
        product.setAverageRating(null);
        product.setCreatedAt(null);
        product.setReviews(null);
        product.setOrderItems(null);

        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
        assertNull(product.getStockQuantity());
        assertNull(product.getCategory());
        assertNull(product.getImageUrl());
        assertNull(product.getAverageRating());
        assertNull(product.getCreatedAt());
        assertNull(product.getReviews());
        assertNull(product.getOrderItems());
    }

    @Test
    void testEmptyCollections() {
        product.setReviews(new HashSet<>());
        product.setOrderItems(new HashSet<>());

        assertNotNull(product.getReviews());
        assertNotNull(product.getOrderItems());
        assertTrue(product.getReviews().isEmpty());
        assertTrue(product.getOrderItems().isEmpty());
    }

    @Test
    void testPriceWithZero() {
        BigDecimal zeroPrice = BigDecimal.ZERO;
        product.setPrice(zeroPrice);
        assertEquals(zeroPrice, product.getPrice());
    }

    @Test
    void testStockQuantityWithZero() {
        Integer zeroStock = 0;
        product.setStockQuantity(zeroStock);
        assertEquals(zeroStock, product.getStockQuantity());
    }

    @Test
    void testAverageRatingBoundaries() {
        // Test minimum rating
        product.setAverageRating(0.0);
        assertEquals(0.0, product.getAverageRating());
        
        // Test maximum rating
        product.setAverageRating(5.0);
        assertEquals(5.0, product.getAverageRating());
        
        // Test decimal rating
        product.setAverageRating(3.75);
        assertEquals(3.75, product.getAverageRating());
    }

    @Test
    void testLargeValues() {
        BigDecimal largePrice = new BigDecimal("999999999.99");
        Integer largeStock = Integer.MAX_VALUE;
        
        product.setPrice(largePrice);
        product.setStockQuantity(largeStock);
        
        assertEquals(largePrice, product.getPrice());
        assertEquals(largeStock, product.getStockQuantity());
    }
}
