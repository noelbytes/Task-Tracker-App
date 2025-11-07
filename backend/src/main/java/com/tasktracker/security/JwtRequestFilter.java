package com.tasktracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Request Filter - validates JWT tokens on every request.
 *
 * This filter intercepts all HTTP requests and:
 * 1. Extracts JWT token from Authorization header
 * 2. Validates the token
 * 3. Sets authentication in Spring Security context if valid
 *
 * Extends OncePerRequestFilter to ensure this filter is executed only once per request.
 *
 * Request Flow:
 * Client → JWT Filter (validate token) → Spring Security → Controller
 */
@Component  // Marks this as a Spring-managed component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    // Service to load user details from database
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    // Utility class for JWT operations (generate, validate, extract)
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Filters each incoming request to validate JWT token.
     *
     * Process:
     * 1. Extract JWT from "Authorization: Bearer <token>" header
     * 2. Extract username from token
     * 3. Load user details from database
     * 4. Validate token (signature, expiration, username match)
     * 5. Set authentication in SecurityContext for downstream filters/controllers
     *
     * If token is invalid or missing, request continues but authentication remains null
     * (SecurityConfig will reject the request for protected endpoints).
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param chain Filter chain to continue processing
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // Extract Authorization header from request
        // Format expected: "Authorization: Bearer <jwt-token>"
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        // Check if Authorization header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract token by removing "Bearer " prefix (7 characters)
            jwt = authorizationHeader.substring(7);
            try {
                // Extract username from JWT token (stored in subject claim)
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Log error but don't stop request - let it fail authentication naturally
                logger.error("JWT Token extraction failed", e);
            }
        }
        
        // If username was extracted and no authentication exists in context yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load full user details from database using username from token
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            // Validate token: check signature, expiration, and username match
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Create authentication token with user details and authorities
                // No credentials needed (token is proof of authentication)
                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Add request details (IP address, session ID, etc.)
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext - user is now authenticated for this request
                // Controllers can now access user via SecurityContextHolder.getContext().getAuthentication()
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continue filter chain - pass request to next filter or controller
        chain.doFilter(request, response);
    }
}

