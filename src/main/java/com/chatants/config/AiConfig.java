package com.chatants.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动装配的bean集合
 *
 * @author Samuel Mau
 */

@Configuration
public class AiConfig {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("You are a friendly chat bot.")
                .build();
    }

    /**
     * 手动配置OpenAiChatModel
     */
    @Bean("openAiChatModel")
    OpenAiChatModel openAiChatModel() {

        var openAiApi = new OpenAiApi(baseUrl, apiKey);
        var openAiChatOptions = OpenAiChatOptions.builder()
                .withModel("gpt-4o")
                .withTemperature(0.4F)
                .withMaxTokens(200)
                .build();
        return new OpenAiChatModel(openAiApi, openAiChatOptions);
    }

}
