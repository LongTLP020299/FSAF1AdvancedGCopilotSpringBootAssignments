package com.fsoft.ecommerce.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserDefaultConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
        assertEquals(Role.USER, newUser.getRole()); // Default role
        assertNotNull(newUser.getCreatedAt()); // Default timestamp
    }

    @Test
    void testUserParameterizedConstructor() {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String firstName = "John";
        String lastName = "Doe";

        User newUser = new User(username, email, password, firstName, lastName);

        assertEquals(username, newUser.getUsername());
        assertEquals(email, newUser.getEmail());
        assertEquals(password, newUser.getPassword());
        assertEquals(firstName, newUser.getFirstName());
        assertEquals(lastName, newUser.getLastName());
        assertEquals(Role.USER, newUser.getRole()); // Default role
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    void testSetAndGetUsername() {
        String username = "testuser";
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        String email = "test@example.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    void testSetAndGetPassword() {
        String password = "password123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    void testSetAndGetFirstName() {
        String firstName = "John";
        user.setFirstName(firstName);
        assertEquals(firstName, user.getFirstName());
    }

    @Test
    void testSetAndGetLastName() {
        String lastName = "Doe";
        user.setLastName(lastName);
        assertEquals(lastName, user.getLastName());
    }

    @Test
    void testSetAndGetRole() {
        Role role = Role.ADMIN;
        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @Test
    void testSetAndGetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        user.setCreatedAt(createdAt);
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    void testSetAndGetOrders() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        
        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        orders.add(order2);
        user.setOrders(orders);
        
        assertEquals(orders, user.getOrders());
        assertEquals(2, user.getOrders().size());
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
        user.setReviews(reviews);
        
        assertEquals(reviews, user.getReviews());
        assertEquals(2, user.getReviews().size());
    }

    @Test
    void testUserDetailsImplementation_GetAuthorities() {
        user.setRole(Role.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testUserDetailsImplementation_UserRole() {
        user.setRole(Role.USER);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserDetailsImplementation_AccountFlags() {
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testUserWithAllFields() {
        Long id = 1L;
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String firstName = "John";
        String lastName = "Doe";
        Role role = Role.ADMIN;
        LocalDateTime createdAt = LocalDateTime.now();

        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setCreatedAt(createdAt);

        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(role, user.getRole());
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    void testUserWithNullValues() {
        user.setId(null);
        user.setUsername(null);
        user.setEmail(null);
        user.setPassword(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setRole(null);
        user.setCreatedAt(null);
        user.setOrders(null);
        user.setReviews(null);

        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getRole());
        assertNull(user.getCreatedAt());
        assertNull(user.getOrders());
        assertNull(user.getReviews());
    }

    @Test
    void testEmptyCollections() {
        user.setOrders(new HashSet<>());
        user.setReviews(new HashSet<>());

        assertNotNull(user.getOrders());
        assertNotNull(user.getReviews());
        assertTrue(user.getOrders().isEmpty());
        assertTrue(user.getReviews().isEmpty());
    }
}
