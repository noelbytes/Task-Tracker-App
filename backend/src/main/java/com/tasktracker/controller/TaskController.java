package com.tasktracker.controller;

import com.tasktracker.dto.TaskDTO;
import com.tasktracker.dto.TaskRequest;
import com.tasktracker.dto.TaskStatsDTO;
import com.tasktracker.model.Task;
import com.tasktracker.service.TaskService;
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
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) String status,
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
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskById(id));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
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
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        try {
            TaskDTO task = taskService.updateTask(id, request);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
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
    
    @GetMapping("/stats")
    public ResponseEntity<TaskStatsDTO> getTaskStats() {
        return ResponseEntity.ok(taskService.getTaskStats());
    }
}

