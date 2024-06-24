package com.chatants.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 *  ChatModel服务层接口
 *
 *  @author Samuel Mau
 */
public interface ChatModelService {
    Flux<ChatResponse> getAnswer(
            String sessionId,
            List<Message> messages,
            String model
    );
}
