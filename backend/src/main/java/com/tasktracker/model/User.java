package com.tasktracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * User Entity - represents a user in the authentication system.
 *
 * JPA Entity mapped to "users" table.
 * Stores user credentials and metadata for authentication/authorization.
 *
 * Security Considerations:
 * - Password stored as BCrypt hash (never plain text)
 * - Username is unique (enforced by database constraint)
 * - Password hash should never be exposed in API responses (use DTOs)
 *
 * Database Design:
 * - Primary key: auto-increment ID
 * - Unique constraint: username (prevents duplicate usernames)
 * - All fields required except those with defaults
 *
 * Relationships:
 * - One user has many tasks (defined in Task entity as ManyToOne)
 */
@Entity  // JPA entity - maps to database table
@Table(name = "users")  // Specifies table name
@Data  // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Lombok: no-args constructor (required by JPA)
@AllArgsConstructor  // Lombok: constructor with all fields
public class User {
    
    // Primary key with auto-increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Username for login - must be unique across all users
    // Database enforces uniqueness via UNIQUE constraint
    @Column(unique = true, nullable = false)
    private String username;
    
    // BCrypt hashed password - NEVER store plain text passwords
    // AuthService uses PasswordEncoder.encode() to hash before saving
    // Spring Security uses PasswordEncoder.matches() to verify during login
    @Column(nullable = false)
    private String password;
    
    // User email address - required field
    @Column(nullable = false)
    private String email;
    
    // User role for authorization (currently only "USER" role used)
    // Could be extended to support "ADMIN", "MODERATOR", etc.
    // Default value is "USER" for new registrations
    @Column(nullable = false)
    private String role = "USER";
    
    // Timestamp of user account creation
    // Automatically set on entity creation (insert)
    // updatable = false prevents accidental changes
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

