package com.tasktracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI-powered task suggestions.
 * 
 * Contains suggested task titles based on user's task history and patterns.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AITaskSuggestionDTO {
    private String[] suggestions;
    private String insight;
}

