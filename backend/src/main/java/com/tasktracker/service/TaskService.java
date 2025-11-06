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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
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
    
    public List<TaskDTO> getAllTasks() {
        User user = getCurrentUser();
        return taskRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public TaskDTO getTaskById(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to task");
        }
        
        return convertToDTO(task);
    }
    
    public TaskDTO createTask(TaskRequest request) {
        User user = getCurrentUser();
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setUser(user);
        
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }
    
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
    }
    
    public List<TaskDTO> getTasksByStatus(Task.TaskStatus status) {
        User user = getCurrentUser();
        return taskRepository.findByUserAndStatus(user, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<TaskDTO> getTasksByPriority(Task.TaskPriority priority) {
        User user = getCurrentUser();
        return taskRepository.findByUserAndPriority(user, priority).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
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
            long totalDuration = completedTasksList.stream()
                    .filter(task -> task.getCompletedAt() != null)
                    .mapToLong(task -> Duration.between(task.getCreatedAt(), task.getCompletedAt()).toHours())
                    .sum();
            averageCompletionTimeHours = (double) totalDuration / completedTasksList.size();
        }
        
        return new TaskStatsDTO(totalTasks, completedTasks, pendingTasks, 
                averageCompletionTimeHours, todoTasks, inProgressTasks);
    }
}

