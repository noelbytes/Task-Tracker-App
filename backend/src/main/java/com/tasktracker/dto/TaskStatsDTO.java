package com.tasktracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatsDTO {
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private double averageCompletionTimeHours;
    private long todoTasks;
    private long inProgressTasks;
}

