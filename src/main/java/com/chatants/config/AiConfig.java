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

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("You are a friendly chat bot.")
                .build();
    }

    @Bean
    OpenAiApi openAiApi(
            @Value("${mau.ai.openai.base-url}") String baseUrl,
            @Value("${mau.ai.openai.api-key}") String apiKey
    ) {
        return new OpenAiApi(baseUrl, apiKey);
    }

}
