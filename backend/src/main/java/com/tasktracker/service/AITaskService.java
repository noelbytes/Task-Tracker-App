package com.tasktracker.service;

import com.tasktracker.model.Task;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI-powered service for intelligent task management features.
 * 
 * Uses Spring AI with OpenAI GPT models to provide:
 * - Natural language task parsing
 * - Smart priority recommendations
 * - Task suggestions based on history
 * - Productivity insights
 * 
 * Interview Points:
 * - Integration with OpenAI API via Spring AI
 * - Prompt engineering for task management domain
 * - Error handling for AI service failures
 * - Cost-effective API usage with token limits
 */
@Service
public class AITaskService {

    private final ChatClient chatClient;

    /**
     * Constructor with ChatClient dependency injection.
     * 
     * ChatClient is provided by Spring AI and configured via properties.
     * 
     * @param chatClient Spring AI chat client for OpenAI integration
     */
    @Autowired
    public AITaskService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Parses natural language text into structured task components.
     * 
     * Example input: "Remind me to buy groceries tomorrow"
     * Expected output: JSON with title, description, priority
     * 
     * @param naturalLanguageInput User's plain text task description
     * @return Structured JSON string with task fields
     */
    public String parseNaturalLanguageTask(String naturalLanguageInput) {
        try {
            String prompt = String.format("""
                Parse the following task description into a structured format.
                Input: "%s"
                
                Extract:
                - title: Short task title (max 50 characters)
                - description: Detailed description
                - priority: LOW, MEDIUM, or HIGH based on urgency keywords
                
                Return ONLY valid JSON in this format:
                {"title": "...", "description": "...", "priority": "MEDIUM"}
                
                If no clear priority is indicated, default to MEDIUM.
                """, naturalLanguageInput);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            // Fallback if AI fails - return basic structured response
            return String.format("""
                {"title": "%s", "description": "%s", "priority": "MEDIUM"}
                """, 
                naturalLanguageInput.substring(0, Math.min(50, naturalLanguageInput.length())),
                naturalLanguageInput);
        }
    }

    /**
     * Recommends task priority based on title and description.
     * 
     * Uses AI to analyze urgency indicators in text:
     * - Keywords: urgent, ASAP, today, tomorrow, deadline
     * - Punctuation: exclamation marks
     * - Context: time-sensitive words
     * 
     * @param title Task title
     * @param description Task description
     * @return Recommended priority (LOW, MEDIUM, HIGH)
     */
    public Task.TaskPriority recommendPriority(String title, String description) {
        try {
            String prompt = String.format("""
                Analyze this task and recommend a priority level.
                Title: "%s"
                Description: "%s"
                
                Consider:
                - Urgency keywords (urgent, ASAP, critical, important)
                - Time sensitivity (today, tomorrow, deadline)
                - Exclamation marks or strong language
                
                Respond with ONLY one word: LOW, MEDIUM, or HIGH
                """, title, description != null ? description : "");

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim()
                    .toUpperCase();

            // Parse AI response to enum
            return switch (response) {
                case "HIGH" -> Task.TaskPriority.HIGH;
                case "LOW" -> Task.TaskPriority.LOW;
                default -> Task.TaskPriority.MEDIUM;
            };
        } catch (Exception e) {
            // Fallback to MEDIUM if AI fails
            return Task.TaskPriority.MEDIUM;
        }
    }

    /**
     * Generates task suggestions based on user's task history.
     * 
     * Analyzes patterns in completed tasks to suggest:
     * - Recurring tasks
     * - Related tasks
     * - Common follow-up tasks
     * 
     * @param recentTasks List of user's recent tasks
     * @return List of suggested task titles
     */
    public List<String> generateTaskSuggestions(List<Task> recentTasks) {
        try {
            // Build context from recent tasks
            StringBuilder tasksContext = new StringBuilder();
            for (Task task : recentTasks.stream().limit(10).toList()) {
                tasksContext.append(String.format("- %s (Status: %s)%n", 
                    task.getTitle(), task.getStatus()));
            }

            String prompt = String.format("""
                Based on these recent tasks:
                %s
                
                Suggest 3 related or follow-up tasks the user might want to add.
                Consider:
                - Task patterns and themes
                - Logical next steps
                - Commonly associated tasks
                
                Return ONLY 3 task titles, one per line, without numbers or bullets.
                """, tasksContext.toString());

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            // Parse response into list
            return response.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .limit(3)
                    .toList();
        } catch (Exception e) {
            // Return empty list if AI fails
            return List.of();
        }
    }

    /**
     * Generates productivity insights based on task completion patterns.
     * 
     * Analyzes:
     * - Completion rates by priority
     * - Time to complete tasks
     * - Task distribution patterns
     * - Productivity trends
     * 
     * @param tasks User's task history
     * @param averageCompletionTime Average time to complete in hours
     * @return AI-generated productivity insight text
     */
    public String generateProductivityInsight(List<Task> tasks, double averageCompletionTime) {
        try {
            long totalTasks = tasks.size();
            long completedTasks = tasks.stream()
                    .filter(t -> t.getStatus() == Task.TaskStatus.DONE)
                    .count();
            long highPriorityTasks = tasks.stream()
                    .filter(t -> t.getPriority() == Task.TaskPriority.HIGH)
                    .count();

            String prompt = String.format("""
                Analyze this task management data and provide a brief productivity insight.
                
                Stats:
                - Total tasks: %d
                - Completed tasks: %d
                - High priority tasks: %d
                - Average completion time: %.1f hours
                
                Provide a friendly, encouraging insight (2-3 sentences) about their productivity.
                Focus on patterns, suggestions for improvement, or positive reinforcement.
                """, totalTasks, completedTasks, highPriorityTasks, averageCompletionTime);

            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            // Fallback generic insight
            return "Keep up the great work! Focus on completing high-priority tasks first to maximize productivity.";
        }
    }

    /**
     * Checks if AI service is available and configured.
     * 
     * Useful for graceful degradation when API key is missing or service is down.
     * 
     * @return true if AI service is ready, false otherwise
     */
    public boolean isAIAvailable() {
        try {
            // Simple test prompt
            chatClient.prompt()
                    .user("test")
                    .call()
                    .content();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

