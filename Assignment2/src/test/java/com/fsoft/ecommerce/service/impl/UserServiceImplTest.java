package com.fsoft.ecommerce.service.impl;

import com.fsoft.ecommerce.entity.User;
import com.fsoft.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
    }
    
    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        
        // Act
        List<User> result = userService.getAllUsers();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository).findAll();
    }
    
    @Test
    void getUserById_ExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        Optional<User> result = userService.getUserById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findById(1L);
    }
    
    @Test
    void getUserById_NonExistingUser_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = userService.getUserById(1L);
        
        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
    }
    
    @Test
    void createUser_ShouldEncodePasswordAndSave() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        User result = userService.createUser(testUser);
        
        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
    }
    
    @Test
    void updateUser_ExistingUser_ShouldUpdateAndSave() {
        // Arrange
        testUser.setUsername("updateduser");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        User result = userService.updateUser(1L, testUser);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).existsById(1L);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
    }
    
    @Test
    void updateUser_NonExistingUser_ShouldReturnNull() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);
        
        // Act
        User result = userService.updateUser(1L, testUser);
        
        // Assert
        assertNull(result);
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void updateUser_WithEmptyPassword_ShouldNotEncodePassword() {
        // Arrange
        testUser.setPassword("");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        User result = userService.updateUser(1L, testUser);
        
        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(testUser);
    }
    
    @Test
    void updateUser_WithNullPassword_ShouldNotEncodePassword() {
        // Arrange
        testUser.setPassword(null);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        User result = userService.updateUser(1L, testUser);
        
        // Assert
        assertNotNull(result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(testUser);
    }
    
    @Test
    void deleteUser_ShouldCallRepository() {
        // Act
        userService.deleteUser(1L);
        
        // Assert
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    void findByUsername_ExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // Act
        Optional<User> result = userService.findByUsername("testuser");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }
    
    @Test
    void findByUsername_NonExistingUser_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = userService.findByUsername("nonexistent");
        
        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByUsername("nonexistent");
    }
    
    @Test
    void findByEmail_ExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // Act
        Optional<User> result = userService.findByEmail("test@example.com");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }
    
    @Test
    void findByEmail_NonExistingUser_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = userService.findByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
}
