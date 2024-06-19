package com.chatants.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 简单的写死的,返回值类型为Flux<String>的聊天接口,格式为event-stream
     */
    @GetMapping(value = "/ai/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> simpleChat() {
        System.out.println("接收到请求");
        return chatClient.prompt()
                .user("给我讲个笑话")
                .stream()
                .content();
    }

    /**
     * 复杂类型,增加默认值,返回格式为直接的流式字符串
     */
    @GetMapping(value = "/ai/stream/complex", produces = "text/sse;charset=UTF-8")
    public Flux<String> complexChat(
            @RequestParam(value = "message", defaultValue = "给我讲个笑话") String message
    ) {
        System.out.println("接收到请求");
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

}
