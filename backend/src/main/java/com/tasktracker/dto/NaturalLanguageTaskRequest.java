package com.tasktracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for natural language task parsing request.
 * 
 * Accepts plain text task description and returns structured task fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaturalLanguageTaskRequest {
    private String text;
}

