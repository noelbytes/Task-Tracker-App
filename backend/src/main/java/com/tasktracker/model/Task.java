package com.tasktracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Task Entity - represents a task in the database.
 *
 * JPA Entity mapped to "tasks" table with automatic ID generation.
 * Uses Lombok annotations to reduce boilerplate (getters, setters, constructors).
 *
 * Database Design:
 * - Primary key: auto-increment ID
 * - Foreign key: user_id references users table
 * - Enums stored as strings for readability
 * - Timestamps for creation and completion tracking
 *
 * Relationships:
 * - Many tasks belong to one user (ManyToOne)
 * - Lazy loading for user to optimize queries
 *
 * Business Rules:
 * - Title is required
 * - Status defaults to TODO
 * - Priority defaults to MEDIUM
 * - createdAt set automatically on insert
 * - completedAt set when status changes to DONE
 */
@Entity  // JPA entity - maps to database table
@Table(name = "tasks")  // Specifies table name
@Data  // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Lombok: generates no-args constructor (required by JPA)
@AllArgsConstructor  // Lombok: generates constructor with all fields
public class Task {
    
    // Primary key with auto-increment (IDENTITY strategy uses database auto-increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Task title - required field (nullable = false enforces NOT NULL constraint)
    @Column(nullable = false)
    private String title;
    
    // Task description - optional, supports up to 1000 characters
    @Column(length = 1000)
    private String description;
    
    // Task status enum stored as string in database (more readable than ordinal)
    // Default value is TODO for new tasks
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;
    
    // Task priority enum stored as string
    // Default value is MEDIUM for new tasks
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;
    
    // Automatically set on entity creation (insert)
    // updatable = false prevents accidental changes on update
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Set when task status changes to DONE
    // Null for incomplete tasks
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Many-to-one relationship: many tasks belong to one user
    // LAZY fetch: user is only loaded when explicitly accessed (performance optimization)
    // JoinColumn specifies foreign key column name
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Task status enumeration.
     *
     * Represents the workflow state of a task:
     * - TODO: Not started
     * - IN_PROGRESS: Currently being worked on
     * - DONE: Completed
     */
    public enum TaskStatus {
        TODO, IN_PROGRESS, DONE
    }
    
    /**
     * Task priority enumeration.
     *
     * Represents the urgency/importance of a task:
     * - LOW: Can be done later
     * - MEDIUM: Normal priority
     * - HIGH: Urgent, should be done soon
     */
    public enum TaskPriority {
        LOW, MEDIUM, HIGH
    }
}

