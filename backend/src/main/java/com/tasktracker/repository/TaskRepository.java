package com.tasktracker.repository;

import com.tasktracker.model.Task;
import com.tasktracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndStatus(User user, Task.TaskStatus status);
    List<Task> findByUserAndPriority(User user, Task.TaskPriority priority);
    long countByUser(User user);
    long countByUserAndStatus(User user, Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.status = 'DONE' AND t.completedAt IS NOT NULL")
    List<Task> findCompletedTasksByUser(User user);
}

