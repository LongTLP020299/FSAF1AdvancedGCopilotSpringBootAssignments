package com.fsoft.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsoft.ecommerce.config.SecurityConfig;
import com.fsoft.ecommerce.dto.UserRegistrationDTO;
import com.fsoft.ecommerce.entity.User;
import com.fsoft.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {AuthController.class, SecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDTO validRegistrationDTO;

    @BeforeEach
    void setUp() {
        validRegistrationDTO = new UserRegistrationDTO();
        validRegistrationDTO.setUsername("testuser123");
        validRegistrationDTO.setEmail("test@example.com");
        validRegistrationDTO.setPassword("StrongP@ss1");
        validRegistrationDTO.setFirstName("John");
        validRegistrationDTO.setLastName("Doe");
    }

    @Test
    void registerUser_WithValidInput_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.createUser(any(User.class))).thenReturn(new User());

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));

        verify(userService).createUser(any(User.class));
    }

    @Test
    void registerUser_WithEmptyEmail_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validRegistrationDTO.setEmail("");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("email")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("required")));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void registerUser_WithInvalidEmailFormat_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validRegistrationDTO.setEmail("invalid-email-format");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("email")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("valid email address")));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void registerUser_WithWeakPassword_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validRegistrationDTO.setPassword("weak"); // No uppercase, digits, or special chars

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("password")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("lowercase letter")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("uppercase letter")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("digit")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("special character")));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void registerUser_WithShortPassword_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validRegistrationDTO.setPassword("Short1@"); // Only 7 characters

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("password")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("between 8 and 128 characters")));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void registerUser_WithInvalidUsername_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validRegistrationDTO.setUsername("ab"); // Too short

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("username")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("between 3 and 50 characters")));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void registerUser_WithInvalidUsernameCharacters_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validRegistrationDTO.setUsername("test@user"); // Contains invalid character

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("username")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("letters, numbers, and underscores")));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void registerUser_WithBlankFirstName_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validRegistrationDTO.setFirstName("");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("firstName")))
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("required")));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        User existingUser = new User();
        when(userService.findByUsername(validRegistrationDTO.getUsername()))
            .thenReturn(Optional.of(existingUser));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Username is already taken!"));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        User existingUser = new User();
        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userService.findByEmail(validRegistrationDTO.getEmail()))
            .thenReturn(Optional.of(existingUser));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegistrationDTO))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));

        verify(userService, never()).createUser(any(User.class));
    }
}
