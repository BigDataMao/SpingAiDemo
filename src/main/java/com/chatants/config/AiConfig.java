package com.chatants.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Simon Mau
 * @Date: 2024/6/17 16:57
 */

@Configuration
public class AiConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("You are a friendly chat bot.")
                .build();
    }

    @Bean
    OpenAiChatModel manualOpenAiChatModel() {
        var openAiApi = new OpenAiApi(System.getenv("OPENAI_API_KEY"));
        var openAiChatOptions = OpenAiChatOptions.builder()
                .withModel("gpt-3.5-turbo")
                .withTemperature(0.4F)
                .withMaxTokens(200)
                .build();
        return new OpenAiChatModel(openAiApi, openAiChatOptions);
    }


}
