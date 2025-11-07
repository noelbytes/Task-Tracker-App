package com.tasktracker.config;

import com.tasktracker.security.CustomUserDetailsService;
import com.tasktracker.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration.
 *
 * Configures JWT-based authentication, password encoding, and authorization rules.
 *
 * Key Security Features:
 * - Stateless session management (no server-side sessions)
 * - JWT token validation on each request
 * - BCrypt password hashing
 * - CORS configuration for cross-origin requests
 * - Public endpoints for login and API documentation
 */
@Configuration  // Marks this as a Spring configuration class
@EnableWebSecurity  // Enables Spring Security
public class SecurityConfig {
    
    // Custom service to load user details from database
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    // Filter that validates JWT tokens on each request
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    // CORS configuration for cross-origin requests
    @Autowired
    private CorsConfig corsConfig;

    /**
     * Defines the password encoder bean.
     *
     * Uses BCrypt hashing algorithm which:
     * - Is one-way (can't be decrypted)
     * - Includes salt automatically
     * - Has adaptive cost factor (can be made slower as computers get faster)
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configures the authentication provider.
     *
     * DaoAuthenticationProvider uses our custom UserDetailsService to load users
     * and compares provided password with stored hash using BCrypt.
     *
     * @return Configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);  // How to load users
        authProvider.setPasswordEncoder(passwordEncoder());  // How to verify passwords
        return authProvider;
    }
    
    /**
     * Exposes the authentication manager as a bean.
     *
     * Used by AuthService to authenticate users during login.
     *
     * @param config Spring's authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Configures the security filter chain - the heart of Spring Security.
     *
     * Defines:
     * - Which endpoints require authentication
     * - Session management strategy
     * - CORS and CSRF settings
     * - Custom filters (JWT validation)
     *
     * @param http HttpSecurity to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS with our custom configuration
            .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
            // Disable CSRF since we're using JWT tokens (not session cookies)
            .csrf(csrf -> csrf.disable())
            // Configure endpoint authorization
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow CORS preflight requests
                .requestMatchers("/api/auth/**", "/h2-console/**", "/actuator/**").permitAll()  // Public endpoints
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()  // API documentation
                .anyRequest().authenticated()  // All other endpoints require authentication
            )
            // Stateless session - no session stored on server, JWT contains all auth info
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Use our custom authentication provider
            .authenticationProvider(authenticationProvider())
            // Add JWT filter before Spring Security's default UsernamePasswordAuthenticationFilter
            // This ensures JWT validation happens before any authentication logic
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Allow H2 console to be embedded in iframe (needed for H2 web console)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        
        return http.build();
    }
}

