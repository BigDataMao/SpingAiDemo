package com.chatants.controller;

import com.chatants.config.LocalCache;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * @description AI聊天接口
 * @author Simon Mau
 * @date 2024/6/17 15:36
 * TODO 流式聊天接口有BUG,会丢字,特别是开始的几轮回答
 */

@RestController
@RequiredArgsConstructor
public class ChatClientController {

    public final ChatClient chatClient;

    /**
     * 简单的写死的,直接返回全部结果,不是流式
     */
    @GetMapping(value = "/ai/simple")
    public String simpleChat(
            @RequestParam(value = "userMessage", defaultValue = "给我讲个笑话") String userMessage
    ) {
        System.out.println("接收到请求");
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * 简单的写死的,返回值类型为Flux<String>的聊天接口,格式为event-stream
     */
    @GetMapping(value = "/ai/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> simpleStreamChat() {
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

    /**
     * 连续对话功能实现
     */
    @GetMapping(value = "/ai/stream/continue", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> continueChat(
            @RequestParam(value = "userMessage", defaultValue = "给我讲个笑话") String userMessage,
            @RequestParam(value = "chatId") String chatId
    ) {
        // 将chatId作为本轮对话的唯一标识,加上用户的输入,缓存本地,实现连续对话
        // 先从本地缓存读取上一轮对话的chatId,如果有,则继续对话,如果没有,则重新开始对话
        // 本地缓存可以使用redis,也可以使用内存缓存
        if (LocalCache.CACHE.get(chatId) != null) {
            // 从缓存中读取上一轮对话的chatId,然后继续对话
            List<Message> messages = (List<Message>) LocalCache.CACHE.get(chatId);
            return getStringFlux(userMessage, chatId, messages);
        } else {
            // 本地为空,则发送新的对话请求,并缓存对话
            List<Message> messages = new ArrayList<>();
            return getStringFlux(userMessage, chatId, messages);
        }
    }

    private Flux<String> getStringFlux(
            @RequestParam(value = "userMessage", defaultValue = "给我讲个笑话") String userMessage,
            @RequestParam("chatId") String chatId, List<Message> messages) {
        messages.add(new UserMessage(userMessage));
        StringBuilder assistMessageBuilder = new StringBuilder();

        return chatClient.prompt(new Prompt(messages))
                .stream()
                .content()
                .doOnNext(assistMessageBuilder::append)
                .doFinally(signalType -> {
                            messages.add(new AssistantMessage(assistMessageBuilder.toString()));
                            System.out.println(assistMessageBuilder);
                            LocalCache.CACHE.put(chatId, messages);
                        }
                );

    }

}
