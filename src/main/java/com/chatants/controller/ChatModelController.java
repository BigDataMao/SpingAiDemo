package com.chatants.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author Simon Mau
 * @description ChatModel的API接口测试用例
 * @date 2024/6/20 21:44
 */
@RestController
public class ChatModelController {
    public final OpenAiChatModel openAiChatModel;

    @Autowired
    public ChatModelController(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @GetMapping("/ai/generate")
    public Map<String,String> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message
    ) {
        return Map.of("generation", openAiChatModel.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message
    ) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return openAiChatModel.stream(prompt);
    }
}
