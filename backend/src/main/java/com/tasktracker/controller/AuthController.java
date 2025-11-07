package com.tasktracker.controller;

import com.tasktracker.dto.LoginRequest;
import com.tasktracker.dto.LoginResponse;
import com.tasktracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Authentication.
 *
 * Handles user login and JWT token generation.
 * This is the only controller with public endpoints (no JWT required).
 */
@RestController  // Indicates this is a REST controller
@RequestMapping("/api/auth")  // Base path for authentication endpoints
@CrossOrigin(origins = "*")  // Allows cross-origin requests
@Tag(name = "Authentication", description = "Authentication endpoints for user login")
public class AuthController {
    
    // Service layer for authentication logic
    @Autowired
    private AuthService authService;
    
    /**
     * Authenticates a user and returns a JWT token.
     *
     * Validates username and password against database.
     * On success, returns JWT token that can be used to access protected endpoints.
     * Token is valid for 24 hours by default.
     *
     * @param request LoginRequest containing username and password
     * @return LoginResponse with JWT token and user details if successful
     */
    @Operation(
            summary = "User login",
            description = "Authenticate user with username and password, returns JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content)
    })
    @PostMapping("/login")  // Maps to POST /api/auth/login - public endpoint
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate user and generate JWT token
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return 400 with error message if authentication fails
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Test endpoint to verify auth service is running.
     *
     * Simple health check endpoint that doesn't require authentication.
     * Useful for testing if the backend is accessible.
     *
     * @return Success message
     */
    @Operation(
            summary = "Test endpoint",
            description = "Simple test endpoint to verify authentication service is working"
    )
    @GetMapping("/test")  // Maps to GET /api/auth/test - public endpoint
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth endpoint is working!");
    }
}

