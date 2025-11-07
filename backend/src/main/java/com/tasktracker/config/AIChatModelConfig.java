package com.tasktracker.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * AI Configuration - selects appropriate ChatModel based on active profile.
 * 
 * Development (default): Uses Ollama (local, free)
 * Production: Uses OpenAI/Groq (cloud, free API)
 * 
 * This resolves the "expected single matching bean but found 2" error
 * by marking the appropriate bean as @Primary for each profile.
 */
@Configuration
public class AIChatModelConfig {

    /**
     * For production profile: Use OpenAI/Groq chat model (cloud-based).
     * Marked as @Primary so Spring uses this when multiple ChatModel beans exist.
     */
    @Bean
    @Primary
    @Profile("prod")
    public ChatModel productionChatModel(@Qualifier("openAiChatModel") ChatModel openAiChatModel) {
        return openAiChatModel;
    }

    /**
     * For default profile (development): Use Ollama chat model (local).
     * Marked as @Primary so Spring uses this when multiple ChatModel beans exist.
     */
    @Bean
    @Primary
    @Profile("default")
    public ChatModel developmentChatModel(@Qualifier("ollamaChatModel") ChatModel ollamaChatModel) {
        return ollamaChatModel;
    }
}

