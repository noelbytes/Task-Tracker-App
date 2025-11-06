package com.tasktracker.dto;

import com.tasktracker.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Status is required")
    private Task.TaskStatus status;
    
    @NotNull(message = "Priority is required")
    private Task.TaskPriority priority;
}

