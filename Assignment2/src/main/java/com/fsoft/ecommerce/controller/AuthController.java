package com.fsoft.ecommerce.controller;

import com.fsoft.ecommerce.dto.UserRegistrationDTO;
import com.fsoft.ecommerce.entity.Role;
import com.fsoft.ecommerce.entity.User;
import com.fsoft.ecommerce.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO, 
                                              BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                String errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                return ResponseEntity.badRequest()
                    .body("Validation errors: " + errorMessages);
            }

            // Check if username already exists
            if (userService.findByUsername(registrationDTO.getUsername()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Error: Username is already taken!");
            }

            // Check if email already exists
            if (userService.findByEmail(registrationDTO.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Error: Email is already in use!");
            }

            // Create new user
            User user = new User();
            user.setUsername(registrationDTO.getUsername());
            user.setEmail(registrationDTO.getEmail());
            user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
            user.setFirstName(registrationDTO.getFirstName());
            user.setLastName(registrationDTO.getLastName());
            user.setRole(Role.USER); // Default role

            userService.createUser(user);

            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not register user - " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Basic login - in production, implement JWT tokens
        try {
            // Validate credentials (implement proper authentication)
            Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    return ResponseEntity.ok("Login successful! User: " + user.getUsername() + " Role: " + user.getRole());
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Error: Invalid credentials!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Error: Authentication failed - " + e.getMessage());
        }
    }

    // Simple login request DTO
    public static class LoginRequest {
        @NotBlank
        private String username;
        
        @NotBlank
        private String password;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
