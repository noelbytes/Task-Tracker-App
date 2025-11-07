package com.tasktracker.service;

import com.tasktracker.dto.LoginRequest;
import com.tasktracker.dto.LoginResponse;
import com.tasktracker.model.User;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service for user login operations.
 *
 * Handles user authentication and JWT token generation.
 * Coordinates between Spring Security's AuthenticationManager and JWT utilities.
 *
 * Authentication Flow:
 * 1. Receive username and password from client
 * 2. Delegate to AuthenticationManager (validates against database)
 * 3. If valid, generate JWT token
 * 4. Return token + user details to client
 *
 * Security Notes:
 * - Passwords are never returned or logged
 * - BCrypt password verification handled by AuthenticationManager
 * - JWT tokens expire after configured time (default 24 hours)
 */
@Service  // Marks this as a Spring service component
public class AuthService {
    
    // Repository for user database operations
    @Autowired
    private UserRepository userRepository;
    
    // BCrypt password encoder for hashing passwords
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Utility for JWT token operations
    @Autowired
    private JwtUtil jwtUtil;
    
    // Spring Security's authentication manager - validates credentials
    @Autowired
    private AuthenticationManager authenticationManager;
    
    /**
     * Authenticates user and generates JWT token.
     *
     * Process:
     * 1. Create authentication token with username/password
     * 2. AuthenticationManager validates against UserDetailsService
     * 3. If valid, load full user details from database
     * 4. Generate JWT token with username
     * 5. Return token + user info in response
     *
     * AuthenticationManager internally:
     * - Loads user via UserDetailsService
     * - Compares provided password with stored BCrypt hash
     * - Throws AuthenticationException if invalid
     *
     * @param request LoginRequest containing username and password
     * @return LoginResponse with JWT token and user details
     * @throws RuntimeException if authentication fails
     */
    public LoginResponse login(LoginRequest request) {
        try {
            // Authenticate user credentials
            // This triggers UserDetailsService.loadUserByUsername() and password verification
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // Load full user entity from database (authentication only validates, doesn't return entity)
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate JWT token with username as subject
            String token = jwtUtil.generateToken(user.getUsername());
            
            // Return token and user details (no password included)
            return new LoginResponse(token, user.getUsername(), user.getEmail(), "Login successful");
        } catch (AuthenticationException e) {
            // AuthenticationException thrown if username not found or password incorrect
            throw new RuntimeException("Invalid username or password");
        }
    }
    
    /**
     * Creates or retrieves demo user for testing.
     *
     * Used by DataInitializer to ensure demo user exists on startup.
     * Idempotent - safe to call multiple times.
     *
     * Demo Credentials:
     * - Username: demo
     * - Password: demo123 (stored as BCrypt hash)
     * - Email: demo@tasktracker.com
     *
     * @return Existing or newly created demo user
     */
    public User createDummyUser() {
        // Check if demo user already exists to avoid duplicates
        if (userRepository.existsByUsername("demo")) {
            return userRepository.findByUsername("demo").get();
        }
        
        // Create new demo user
        User user = new User();
        user.setUsername("demo");
        user.setPassword(passwordEncoder.encode("demo123"));  // BCrypt hash password
        user.setEmail("demo@tasktracker.com");
        user.setRole("USER");
        
        return userRepository.save(user);
    }
}

