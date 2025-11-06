package com.tasktracker.config;

import com.tasktracker.model.Task;
import com.tasktracker.model.User;
import com.tasktracker.repository.TaskRepository;
import com.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create dummy user if not exists
        if (!userRepository.existsByUsername("demo")) {
            User user = new User();
            user.setUsername("demo");
            user.setPassword(passwordEncoder.encode("demo123"));
            user.setEmail("demo@tasktracker.com");
            user.setRole("USER");
            userRepository.save(user);
            
            // Create some sample tasks
            createSampleTask(user, "Complete project documentation", 
                "Write comprehensive documentation for the project", 
                Task.TaskStatus.IN_PROGRESS, Task.TaskPriority.HIGH);
            
            createSampleTask(user, "Review pull requests", 
                "Review and merge pending pull requests", 
                Task.TaskStatus.TODO, Task.TaskPriority.MEDIUM);
            
            createSampleTask(user, "Fix bug in authentication", 
                "Fix the JWT token expiration issue", 
                Task.TaskStatus.DONE, Task.TaskPriority.HIGH);
            
            createSampleTask(user, "Update dependencies", 
                "Update all project dependencies to latest versions", 
                Task.TaskStatus.TODO, Task.TaskPriority.LOW);
            
            createSampleTask(user, "Prepare demo presentation", 
                "Create slides for the product demo", 
                Task.TaskStatus.IN_PROGRESS, Task.TaskPriority.MEDIUM);
            
            System.out.println("Sample data initialized successfully!");
            System.out.println("Demo User Created - Username: demo, Password: demo123");
        }
    }
    
    private void createSampleTask(User user, String title, String description, 
                                  Task.TaskStatus status, Task.TaskPriority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setUser(user);
        
        if (status == Task.TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now().minusDays(1));
        }
        
        taskRepository.save(task);
    }
}

