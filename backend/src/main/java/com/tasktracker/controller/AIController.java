package com.tasktracker.controller;

import com.tasktracker.dto.AITaskSuggestionDTO;
import com.tasktracker.dto.NaturalLanguageTaskRequest;
import com.tasktracker.model.Task;
import com.tasktracker.service.AITaskService;
import com.tasktracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for AI-powered task features.
 * 
 * Provides endpoints for:
 * - Natural language task creation
 * - Smart task suggestions
 * - Priority recommendations
 * - Productivity insights
 * 
 * Requires JWT authentication for all endpoints.
 * 
 * Interview Points:
 * - Integration of AI services into REST API
 * - Graceful degradation when AI unavailable
 * - Error handling for external API calls
 * - Separation of AI logic in service layer
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
@Tag(name = "AI Features", description = "AI-powered intelligent task management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AIController {

    @Autowired
    private AITaskService aiTaskService;

    @Autowired
    private TaskService taskService;

    /**
     * Parses natural language text into structured task fields.
     * 
     * Example: POST /api/ai/parse-task
     * Body: {"text": "Remind me to buy groceries tomorrow"}
     * Response: {"title": "Buy groceries", "description": "...", "priority": "MEDIUM"}
     * 
     * @param request Natural language task description
     * @return Structured task fields as JSON
     */
    @Operation(
            summary = "Parse natural language to task",
            description = "Convert plain text into structured task fields using AI"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully parsed task"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "503", description = "AI service unavailable")
    })
    @PostMapping("/parse-task")
    public ResponseEntity<?> parseNaturalLanguageTask(@RequestBody NaturalLanguageTaskRequest request) {
        try {
            if (request.getText() == null || request.getText().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Text field is required");
                return ResponseEntity.badRequest().body(error);
            }

            // Call AI service to parse natural language
            String parsedJson = aiTaskService.parseNaturalLanguageTask(request.getText());
            
            return ResponseEntity.ok(parsedJson);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to parse task: " + e.getMessage());
            return ResponseEntity.status(503).body(error);
        }
    }

    /**
     * Generates smart task suggestions based on user's history.
     * 
     * Analyzes recent tasks and suggests related or follow-up tasks.
     * 
     * @return List of suggested task titles and productivity insight
     */
    @Operation(
            summary = "Get AI task suggestions",
            description = "Get intelligent task suggestions based on your task history"
    )
    @ApiResponse(responseCode = "200", description = "Successfully generated suggestions")
    @GetMapping("/suggestions")
    public ResponseEntity<AITaskSuggestionDTO> getTaskSuggestions() {
        try {
            // Get user's recent tasks for context
            List<com.tasktracker.dto.TaskDTO> recentTaskDTOs = taskService.getAllTasks();
            
            // Convert DTOs back to entities for AI processing (simplified)
            List<Task> recentTasks = recentTaskDTOs.stream()
                    .map(dto -> {
                        Task task = new Task();
                        task.setTitle(dto.getTitle());
                        task.setStatus(dto.getStatus());
                        task.setPriority(dto.getPriority());
                        return task;
                    })
                    .toList();

            // Generate AI suggestions
            List<String> suggestions = aiTaskService.generateTaskSuggestions(recentTasks);
            
            // Generate productivity insight
            String insight = "Based on your task patterns, here are some suggestions to help you stay organized.";
            if (!recentTasks.isEmpty()) {
                // Only call AI if we have tasks
                double avgTime = taskService.getTaskStats().getAverageCompletionTimeHours();
                insight = aiTaskService.generateProductivityInsight(recentTasks, avgTime);
            }

            AITaskSuggestionDTO response = new AITaskSuggestionDTO(
                    suggestions.toArray(new String[0]),
                    insight
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return empty suggestions if AI fails
            AITaskSuggestionDTO fallback = new AITaskSuggestionDTO(
                    new String[]{},
                    "Keep up the great work on your tasks!"
            );
            return ResponseEntity.ok(fallback);
        }
    }

    /**
     * Recommends priority level for a task based on AI analysis.
     * 
     * Analyzes title and description for urgency indicators.
     * 
     * @param title Task title
     * @param description Task description (optional)
     * @return Recommended priority (LOW, MEDIUM, HIGH)
     */
    @Operation(
            summary = "Get priority recommendation",
            description = "AI analyzes task text and recommends a priority level"
    )
    @ApiResponse(responseCode = "200", description = "Successfully recommended priority")
    @GetMapping("/recommend-priority")
    public ResponseEntity<Map<String, String>> recommendPriority(
            @RequestParam String title,
            @RequestParam(required = false) String description) {
        try {
            Task.TaskPriority priority = aiTaskService.recommendPriority(title, description);
            
            Map<String, String> response = new HashMap<>();
            response.put("recommendedPriority", priority.name());
            response.put("title", title);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("recommendedPriority", "MEDIUM");
            response.put("message", "Using default priority");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Generates productivity insight based on task history.
     * 
     * Analyzes completion patterns and provides actionable advice.
     * 
     * @return AI-generated productivity insight
     */
    @Operation(
            summary = "Get productivity insight",
            description = "Get AI-generated insights about your task completion patterns"
    )
    @ApiResponse(responseCode = "200", description = "Successfully generated insight")
    @GetMapping("/productivity-insight")
    public ResponseEntity<Map<String, String>> getProductivityInsight() {
        try {
            // Get user's tasks
            List<com.tasktracker.dto.TaskDTO> taskDTOs = taskService.getAllTasks();
            List<Task> tasks = taskDTOs.stream()
                    .map(dto -> {
                        Task task = new Task();
                        task.setTitle(dto.getTitle());
                        task.setStatus(dto.getStatus());
                        task.setPriority(dto.getPriority());
                        return task;
                    })
                    .toList();

            double avgTime = taskService.getTaskStats().getAverageCompletionTimeHours();
            String insight = aiTaskService.generateProductivityInsight(tasks, avgTime);
            
            Map<String, String> response = new HashMap<>();
            response.put("insight", insight);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("insight", "Keep focusing on your priorities and you'll continue making progress!");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Health check endpoint to verify AI service availability.
     * 
     * Useful for frontend to conditionally enable/disable AI features.
     * 
     * @return Status of AI service
     */
    @Operation(
            summary = "Check AI service status",
            description = "Verify if AI features are available"
    )
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAIStatus() {
        boolean available = aiTaskService.isAIAvailable();
        
        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("provider", "OpenAI");
        response.put("model", "gpt-3.5-turbo");
        
        return ResponseEntity.ok(response);
    }
}

