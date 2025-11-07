package com.tasktracker.controller;

import com.tasktracker.dto.TaskDTO;
import com.tasktracker.dto.TaskRequest;
import com.tasktracker.dto.TaskStatsDTO;
import com.tasktracker.model.Task;
import com.tasktracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Task Management.
 *
 * Handles HTTP requests for task CRUD operations, filtering, and statistics.
 * Uses JWT authentication for all endpoints via SecurityRequirement annotation.
 * Returns DTOs instead of entities to avoid exposing internal database structure.
 */
@RestController  // Marks this as a REST controller - combines @Controller and @ResponseBody
@RequestMapping("/api/tasks")  // Base URL path for all endpoints in this controller
@CrossOrigin(origins = "*")  // Allows cross-origin requests (CORS handled globally in SecurityConfig)
@Tag(name = "Tasks", description = "Task management endpoints")  // Swagger documentation grouping
@SecurityRequirement(name = "Bearer Authentication")  // Indicates JWT required for all endpoints
public class TaskController {
    
    // Service layer dependency - handles business logic and database operations
    @Autowired
    private TaskService taskService;
    
    /**
     * Retrieves all tasks for the authenticated user with optional filtering.
     *
     * Supports filtering by either status OR priority (not both simultaneously).
     * If no filters provided, returns all tasks for the current user.
     * User context is extracted from JWT token in the service layer.
     *
     * @param status Optional filter by task status (TODO, IN_PROGRESS, DONE)
     * @param priority Optional filter by task priority (LOW, MEDIUM, HIGH)
     * @return List of TaskDTOs matching the filter criteria
     */
    @Operation(
            summary = "Get all tasks",
            description = "Retrieve all tasks with optional filtering by status or priority"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @GetMapping  // Maps to GET /api/tasks
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @Parameter(description = "Filter by task status (TODO, IN_PROGRESS, DONE)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter by task priority (LOW, MEDIUM, HIGH)")
            @RequestParam(required = false) String priority) {
        
        // Filter by status if provided
        if (status != null) {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(taskService.getTasksByStatus(taskStatus));
        }
        
        // Filter by priority if provided
        if (priority != null) {
            Task.TaskPriority taskPriority = Task.TaskPriority.valueOf(priority.toUpperCase());
            return ResponseEntity.ok(taskService.getTasksByPriority(taskPriority));
        }
        
        // No filters - return all tasks for current user
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    /**
     * Retrieves a specific task by its ID.
     *
     * Verifies that the task belongs to the authenticated user before returning.
     * Returns 404 if task not found or doesn't belong to current user.
     *
     * @param id The unique identifier of the task
     * @return TaskDTO if found and authorized, error message otherwise
     */
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")  // Maps to GET /api/tasks/{id} - {id} is a path variable
    public ResponseEntity<?> getTaskById(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskById(id));
        } catch (Exception e) {
            // Return error response if task not found or unauthorized
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    /**
     * Creates a new task for the authenticated user.
     *
     * Automatically associates the task with the current user from JWT token.
     * Validates request body using @Valid annotation with Bean Validation.
     * Returns HTTP 201 Created on success with the created task.
     *
     * @param request TaskRequest containing title, description, status, and priority
     * @return Created TaskDTO with generated ID and timestamps
     */
    @Operation(summary = "Create new task", description = "Create a new task with title, description, status, and priority")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping  // Maps to POST /api/tasks
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest request) {
        try {
            TaskDTO task = taskService.createTask(request);
            // Return 201 Created status with the new task
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (Exception e) {
            // Return 400 Bad Request with error message
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Updates an existing task.
     *
     * Verifies task belongs to authenticated user before updating.
     * Automatically sets completion timestamp when status changes to DONE.
     * Clears completion timestamp if status changes from DONE to another status.
     *
     * @param id The ID of the task to update
     * @param request TaskRequest containing updated fields
     * @return Updated TaskDTO with new values
     */
    @Operation(summary = "Update task", description = "Update an existing task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")  // Maps to PUT /api/tasks/{id}
    public ResponseEntity<?> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        try {
            TaskDTO task = taskService.updateTask(id, request);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Deletes a task by ID.
     *
     * Verifies task belongs to authenticated user before deletion.
     * Permanent deletion - cannot be undone.
     *
     * @param id The ID of the task to delete
     * @return Success message if deleted
     */
    @Operation(summary = "Delete task", description = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")  // Maps to DELETE /api/tasks/{id}
    public ResponseEntity<?> deleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            // Return success message
            Map<String, String> response = new HashMap<>();
            response.put("message", "Task deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Retrieves task statistics for the authenticated user.
     *
     * Calculates and returns:
     * - Total task count
     * - Completed vs pending tasks
     * - Tasks by status (TODO, IN_PROGRESS, DONE)
     * - Average completion time in hours
     *
     * @return TaskStatsDTO containing all statistics
     */
    @Operation(
            summary = "Get task statistics",
            description = "Get statistics including total tasks, completed/pending counts, and average completion time"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    @GetMapping("/stats")  // Maps to GET /api/tasks/stats
    public ResponseEntity<TaskStatsDTO> getTaskStats() {
        return ResponseEntity.ok(taskService.getTaskStats());
    }
}

