package com.tasf.prototipoDesarrollo.Especialista;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class GeminiConfig {

    @Value("${langchain4j.google.ai.gemini.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.google.ai.gemini.chat-model.model-name}")
    private String modelName;

    @Bean
    public ChatLanguageModel geminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.0) // Temperatura baja para código más preciso
                .timeout(Duration.ofSeconds(600))
                .build();
    }
}