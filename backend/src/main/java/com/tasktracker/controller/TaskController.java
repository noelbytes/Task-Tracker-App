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

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "Task management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Operation(
            summary = "Get all tasks",
            description = "Retrieve all tasks with optional filtering by status or priority"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @Parameter(description = "Filter by task status (TODO, IN_PROGRESS, DONE)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter by task priority (LOW, MEDIUM, HIGH)")
            @RequestParam(required = false) String priority) {
        
        if (status != null) {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(taskService.getTasksByStatus(taskStatus));
        }
        
        if (priority != null) {
            Task.TaskPriority taskPriority = Task.TaskPriority.valueOf(priority.toUpperCase());
            return ResponseEntity.ok(taskService.getTasksByPriority(taskPriority));
        }
        
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskById(id));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @Operation(summary = "Create new task", description = "Create a new task with title, description, status, and priority")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest request) {
        try {
            TaskDTO task = taskService.createTask(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @Operation(summary = "Update task", description = "Update an existing task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
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
    
    @Operation(summary = "Delete task", description = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Task deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @Operation(
            summary = "Get task statistics",
            description = "Get statistics including total tasks, completed/pending counts, and average completion time"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    @GetMapping("/stats")
    public ResponseEntity<TaskStatsDTO> getTaskStats() {
        return ResponseEntity.ok(taskService.getTaskStats());
    }
}

