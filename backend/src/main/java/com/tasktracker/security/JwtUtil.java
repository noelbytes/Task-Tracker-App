package com.tasktracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility Class for Token Operations.
 *
 * Handles JWT token generation, validation, and claim extraction.
 * Uses JJWT library with HMAC-SHA algorithm for signing tokens.
 *
 * Key Features:
 * - Token generation with configurable expiration
 * - Token validation (signature + expiration check)
 * - Claim extraction (username, expiration, custom claims)
 * - Secure HMAC-SHA256 signing
 *
 * Token Structure:
 * Header.Payload.Signature
 * - Header: algorithm and token type
 * - Payload: claims (username, issued at, expiration)
 * - Signature: HMAC-SHA256(header + payload, secret)
 */
@Component  // Spring-managed component available for dependency injection
public class JwtUtil {
    
    // JWT secret key from application.properties - used for signing tokens
    @Value("${jwt.secret}")
    private String secret;
    
    // Token expiration time in milliseconds (default: 24 hours = 86400000ms)
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * Creates signing key from secret string.
     *
     * Converts secret string to SecretKey for HMAC-SHA256 algorithm.
     * Key must be at least 256 bits (32 bytes) for HS256.
     *
     * @return SecretKey for signing/verifying JWT tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Extracts username from JWT token.
     *
     * Username is stored in the "subject" claim of the token.
     * Used to identify which user the token belongs to.
     *
     * @param token JWT token string
     * @return Username from token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extracts expiration date from JWT token.
     *
     * @param token JWT token string
     * @return Expiration date from token claims
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Generic method to extract any claim from JWT token.
     *
     * Uses a function to specify which claim to extract.
     * Example: extractClaim(token, Claims::getSubject)
     *
     * @param token JWT token string
     * @param claimsResolver Function to extract specific claim
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Parses and extracts all claims from JWT token.
     *
     * Validates token signature using signing key.
     * Throws exception if token is invalid or signature doesn't match.
     *
     * @param token JWT token string
     * @return Claims object containing all token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // Verify signature with secret key
                .build()
                .parseSignedClaims(token)  // Parse and validate token
                .getPayload();  // Extract claims from payload
    }
    
    /**
     * Checks if JWT token has expired.
     *
     * Compares token's expiration date with current date.
     *
     * @param token JWT token string
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Generates JWT token for a username.
     *
     * Creates token with:
     * - Subject: username
     * - Issued at: current time
     * - Expiration: current time + configured expiration
     * - Signature: HMAC-SHA256 with secret key
     *
     * @param username Username to include in token
     * @return Generated JWT token string
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    
    /**
     * Creates JWT token with custom claims and subject.
     *
     * Token includes:
     * - Custom claims (can add roles, permissions, etc.)
     * - Subject (username)
     * - Issued at timestamp
     * - Expiration timestamp
     * - HMAC-SHA256 signature
     *
     * @param claims Custom claims to include in token
     * @param subject Subject (username) for the token
     * @return Compact JWT token string (Base64 encoded)
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)  // Add custom claims
                .subject(subject)  // Set username as subject
                .issuedAt(new Date(System.currentTimeMillis()))  // Token creation time
                .expiration(new Date(System.currentTimeMillis() + expiration))  // Token expiry
                .signWith(getSigningKey())  // Sign with secret key
                .compact();  // Build and encode to string
    }
    
    /**
     * Validates JWT token against user details.
     *
     * Checks:
     * 1. Username in token matches UserDetails username
     * 2. Token has not expired
     *
     * Note: Signature validation happens in extractUsername/extractAllClaims.
     * If signature is invalid, those methods throw exception.
     *
     * @param token JWT token string
     * @param userDetails User details from database
     * @return true if token is valid for this user, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

