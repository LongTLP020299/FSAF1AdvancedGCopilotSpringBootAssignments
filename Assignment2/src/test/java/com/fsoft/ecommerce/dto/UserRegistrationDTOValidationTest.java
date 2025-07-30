package com.fsoft.ecommerce.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUserRegistrationDTO_ShouldPassValidation() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("testUser123");
        dto.setEmail("test@example.com");
        dto.setPassword("StrongP@ss1");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        // Act
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Valid DTO should not have validation errors");
    }

    @Test
    void invalidUsername_ShouldFailValidation() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("ab"); // Too short
        dto.setEmail("test@example.com");
        dto.setPassword("StrongP@ss1");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        // Act
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("testUser123");
        dto.setEmail("invalid-email"); // Invalid email format
        dto.setPassword("StrongP@ss1");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        // Act
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void weakPassword_ShouldFailValidation() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("testUser123");
        dto.setEmail("test@example.com");
        dto.setPassword("weak"); // Too weak - no uppercase, digits, or special chars
        dto.setFirstName("John");
        dto.setLastName("Doe");

        // Act
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void invalidFirstName_ShouldFailValidation() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("testUser123");
        dto.setEmail("test@example.com");
        dto.setPassword("StrongP@ss1");
        dto.setFirstName("John123"); // Contains numbers
        dto.setLastName("Doe");

        // Act
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    void blankFields_ShouldFailValidation() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("");
        dto.setEmail("");
        dto.setPassword("");
        dto.setFirstName("");
        dto.setLastName("");

        // Act
        Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.size() >= 5, "Should have at least 5 validation errors for the 5 fields");
        
        // Check that each field has at least one violation
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
    }
}
