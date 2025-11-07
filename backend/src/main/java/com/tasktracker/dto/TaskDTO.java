package com.tasktracker.dto;

import com.tasktracker.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}

