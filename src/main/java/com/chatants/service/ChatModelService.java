package com.chatants.service;

import com.chatants.config.LocalCache;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
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
            List<Message> messages
    );
}
