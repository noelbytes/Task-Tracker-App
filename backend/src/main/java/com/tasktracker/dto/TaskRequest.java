package com.tasktracker.dto;

import com.tasktracker.model.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating or updating a task")
public class TaskRequest {
    @Schema(description = "Task title", example = "Complete project documentation", required = true)
    @NotBlank(message = "Title is required")
    private String title;
    
    @Schema(description = "Task description", example = "Write comprehensive API documentation using Swagger")
    private String description;
    
    @Schema(description = "Task status", example = "TODO", allowableValues = {"TODO", "IN_PROGRESS", "DONE"}, required = true)
    @NotNull(message = "Status is required")
    private Task.TaskStatus status;
    
    @Schema(description = "Task priority", example = "MEDIUM", allowableValues = {"LOW", "MEDIUM", "HIGH"}, required = true)
    @NotNull(message = "Priority is required")
    private Task.TaskPriority priority;
}

