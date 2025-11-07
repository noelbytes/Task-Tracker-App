package com.tasktracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) Configuration.
 *
 * Configures which frontend origins can access the backend API.
 * Required because frontend and backend run on different origins in development and production.
 *
 * CORS Basics:
 * - Browsers enforce Same-Origin Policy by default (security feature)
 * - CORS headers tell browser which cross-origin requests to allow
 * - Backend must explicitly allow frontend's origin
 *
 * Configuration:
 * - Allowed origins from application.properties (comma-separated)
 * - Allows all standard HTTP methods
 * - Allows credentials (cookies, Authorization header)
 * - Preflight requests cached for 1 hour
 *
 * Security Notes:
 * - Never use "*" for allowedOrigins in production
 * - Always specify exact frontend URLs
 * - allowCredentials requires specific origins (can't use "*")
 */
@Configuration  // Spring configuration class
public class CorsConfig {
    
    // Comma-separated list of allowed frontend URLs from application.properties
    // Example: "http://localhost:4200,https://tasktracker-frontend.onrender.com"
    @Value("${cors.allowed.origins}")
    private String allowedOrigins;
    
    /**
     * Creates CORS configuration source for Spring Security.
     *
     * Defines CORS policy for all endpoints ("/**").
     * Called by SecurityConfig to enable CORS in security filter chain.
     *
     * Configuration Details:
     * - AllowedOrigins: Frontend URLs that can call this API
     * - AllowedMethods: HTTP methods accepted (GET, POST, PUT, DELETE, etc.)
     * - AllowedHeaders: All request headers allowed (Authorization, Content-Type, etc.)
     * - AllowCredentials: true (allows Authorization header and cookies)
     * - MaxAge: 3600s (preflight requests cached for 1 hour)
     *
     * Preflight Requests:
     * - Browser sends OPTIONS request before actual request
     * - Checks if server allows the cross-origin request
     * - Cached for MaxAge duration to reduce overhead
     *
     * @return Configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse comma-separated origins and set as allowed origins
        // Example: "http://localhost:4200,https://frontend.com" → ["http://localhost:4200", "https://frontend.com"]
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // Allow all standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers (Authorization, Content-Type, etc.)
        // "*" is safe here because we control allowed origins
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (Authorization header, cookies)
        // Required for JWT authentication
        configuration.setAllowCredentials(true);

        // Cache preflight responses for 1 hour (reduces OPTIONS requests)
        configuration.setMaxAge(3600L);
        
        // Register CORS configuration for all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // Apply to all paths
        return source;
    }
}

