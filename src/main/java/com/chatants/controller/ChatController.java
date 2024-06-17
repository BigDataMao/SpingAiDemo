package com.chatants.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @Description:
 * @Author: Simon Mau
 * @Date: 2024/6/17 15:36
 */

@RestController
@RequiredArgsConstructor
public class ChatController {

    public final ChatClient chatClient;

    @GetMapping(value = "/ai/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> simpleChat() {
        System.out.println("接收到请求");
        return chatClient.prompt()
                .user("给我讲个笑话")
                .stream()
                .content();
    }

}
