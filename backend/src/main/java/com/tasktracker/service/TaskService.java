package com.tasktracker.service;

import com.tasktracker.dto.TaskDTO;
import com.tasktracker.dto.TaskRequest;
import com.tasktracker.dto.TaskStatsDTO;
import com.tasktracker.model.Task;
import com.tasktracker.model.User;
import com.tasktracker.repository.TaskRepository;
import com.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Task business logic.
 *
 * Handles all task-related operations including CRUD, filtering, and statistics.
 * Ensures users can only access their own tasks.
 * Converts between Entity and DTO objects to maintain separation of concerns.
 */
@Service  // Marks this as a Spring service component
public class TaskService {
    
    // Repository for task database operations
    @Autowired
    private TaskRepository taskRepository;
    
    // Repository for user database operations
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CacheManager cacheManager;

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * Extracts username from JWT token in SecurityContext and fetches User entity.
     * This ensures all operations are performed in the context of the authenticated user.
     *
     * @return User entity of the authenticated user
     * @throws RuntimeException if user not found in database
     */
    private User getCurrentUser() {
        // Get username from JWT token stored in SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /**
     * Converts Task entity to TaskDTO for API response.
     *
     * DTOs prevent exposing internal entity structure and relationships.
     * This provides better security and allows API changes without affecting database.
     *
     * @param task Task entity from database
     * @return TaskDTO with mapped fields
     */
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setCompletedAt(task.getCompletedAt());
        return dto;
    }
    
    /**
     * Retrieves all tasks for the authenticated user.
     *
     * @return List of TaskDTOs belonging to current user
     */
    @Cacheable(value = "tasksByUser", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()", unless = "#result == null || #result.isEmpty()")
    public List<TaskDTO> getAllTasks() {
        User user = getCurrentUser();
        // Query database for user's tasks and convert to DTOs using Stream API
        return taskRepository.findByUser(user).stream()
                .map(this::convertToDTO)  // Method reference for conversion
                .toList();
    }
    
    /**
     * Retrieves a specific task by ID with authorization check.
     *
     * @param id Task ID to retrieve
     * @return TaskDTO if found and user is authorized
     * @throws RuntimeException if task not found or user unauthorized
     */
    @Cacheable(value = "taskById", key = "#id")
    public TaskDTO getTaskById(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Security check: verify task belongs to authenticated user
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to task");
        }
        
        return convertToDTO(task);
    }
    
    private void evictUserTaskCaches() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Cache tasksCache = cacheManager.getCache("tasksByUser");
        if (tasksCache != null) {
            tasksCache.evict(username); // all tasks
            for (Task.TaskStatus s : Task.TaskStatus.values()) {
                tasksCache.evict(username + ":status:" + s);
            }
            for (Task.TaskPriority p : Task.TaskPriority.values()) {
                tasksCache.evict(username + ":priority:" + p);
            }
        }
        Cache statsCache = cacheManager.getCache("taskStats");
        if (statsCache != null) {
            statsCache.evict(username);
        }
    }

    @CachePut(value = "taskById", key = "#result.id")
    public TaskDTO createTask(TaskRequest request) {
        User user = getCurrentUser();
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setUser(user);
        
        Task savedTask = taskRepository.save(task);
        evictUserTaskCaches();
        return convertToDTO(savedTask);
    }
    
    @Caching(put = {
            @CachePut(value = "taskById", key = "#id")
    })
    public TaskDTO updateTask(Long id, TaskRequest request) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to task");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        
        // If status changes to DONE, set completion time
        if (request.getStatus() == Task.TaskStatus.DONE && task.getStatus() != Task.TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (request.getStatus() != Task.TaskStatus.DONE && task.getStatus() == Task.TaskStatus.DONE) {
            task.setCompletedAt(null);
        }
        
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        
        Task updatedTask = taskRepository.save(task);
        evictUserTaskCaches();
        return convertToDTO(updatedTask);
    }
    
    public void deleteTask(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to task");
        }
        
        taskRepository.delete(task);
        evictUserTaskCaches();
    }
    
    @Cacheable(value = "tasksByUser", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName() + ':status:' + #status", unless = "#result == null || #result.isEmpty()")
    public List<TaskDTO> getTasksByStatus(Task.TaskStatus status) {
        User user = getCurrentUser();
        return taskRepository.findByUserAndStatus(user, status).stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Cacheable(value = "tasksByUser", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName() + ':priority:' + #priority", unless = "#result == null || #result.isEmpty()")
    public List<TaskDTO> getTasksByPriority(Task.TaskPriority priority) {
        User user = getCurrentUser();
        return taskRepository.findByUserAndPriority(user, priority).stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Cacheable(value = "taskStats", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public TaskStatsDTO getTaskStats() {
        User user = getCurrentUser();
        
        long totalTasks = taskRepository.countByUser(user);
        long completedTasks = taskRepository.countByUserAndStatus(user, Task.TaskStatus.DONE);
        long todoTasks = taskRepository.countByUserAndStatus(user, Task.TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByUserAndStatus(user, Task.TaskStatus.IN_PROGRESS);
        long pendingTasks = totalTasks - completedTasks;
        
        // Calculate average completion time
        List<Task> completedTasksList = taskRepository.findCompletedTasksByUser(user);
        double averageCompletionTimeHours = 0.0;
        
        if (!completedTasksList.isEmpty()) {
            // Use milliseconds for precision instead of truncated hours
            // Filter to only include tasks with both timestamps present
            List<Task> tasksWithTimestamps = completedTasksList.stream()
                    .filter(task -> task.getCreatedAt() != null && task.getCompletedAt() != null)
                    .toList();

            if (!tasksWithTimestamps.isEmpty()) {
                // Calculate total duration in milliseconds
                long totalMillis = tasksWithTimestamps.stream()
                        .mapToLong(task -> Duration.between(task.getCreatedAt(), task.getCompletedAt()).toMillis())
                        .sum();

                // Calculate average in milliseconds, then convert to hours
                double averageMillis = (double) totalMillis / tasksWithTimestamps.size();
                averageCompletionTimeHours = averageMillis / (1000.0 * 60.0 * 60.0); // Convert ms to hours
            }
        }
        
        return new TaskStatsDTO(totalTasks, completedTasks, pendingTasks, 
                averageCompletionTimeHours, todoTasks, inProgressTasks);
    }
}
