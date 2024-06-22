package com.chatants.controller;

import com.chatants.config.LocalCache;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
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

    /**
     * @description 测试生成聊天
     * @param message  用户输入的消息
     */
    @GetMapping("/ai/generate")
    public Map<String,String> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message
    ) {
        return Map.of("generation", openAiChatModel.call(message));
    }

    /**
     * @description 流式返回聊天,并能后端留存数据
     * @param message  用户输入的消息
     */
    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(
            @RequestParam(value = "message", defaultValue = "用一个四字成语夸我") String message
    ) {
        Prompt prompt = new Prompt(new UserMessage(message));
        StringBuilder assistantMessage = new StringBuilder();
        return openAiChatModel.stream(prompt)
                .doOnNext(chatResponse -> assistantMessage.append(chatResponse.getResult().getOutput().getContent()))
                .doOnComplete(() -> System.out.println("assistantMessage = " + assistantMessage));
    }

    /**
     * @description 流式返回聊天,并能后端留存数据
     * @param message  用户输入的消息
     * @param sessionId  会话ID
     */
    @GetMapping("/ai/generateStreamWithSessionId")
    public Flux<ChatResponse> generateStreamWithSessionId(
            @RequestParam(value = "message", defaultValue = "用一个四字成语夸我") String message,
            @RequestParam(value = "sessionId", defaultValue = "1") String sessionId
    ) {
        List<Message> messages = LocalCache.getMessageListFromCache(sessionId);
        messages.add(new UserMessage(message));
        System.out.printf("messages = %s%n", messages);

        return getAnswer(sessionId, messages);
    }


    public Flux<ChatResponse> getAnswer(
            String sessionId,
            List<Message> messages
    ){
        StringBuilder answer = new StringBuilder();
        Prompt prompt = new Prompt(messages);
        return openAiChatModel.stream(prompt)
                .doOnNext(chatResponse -> {
                    String tmpContent = chatResponse.getResult().getOutput().getContent();
                    // 通字符串会带双引号,如果是null则不会带双引号.通常结尾的null不要加到字符串中
                    if (tmpContent != null){answer.append(tmpContent);}
                })
                .doOnComplete(() -> {
                    System.out.printf("answer = %s%n", answer);
                    messages.add(new AssistantMessage(answer.toString()));
                    LocalCache.CACHE.put(sessionId, messages);
                });

    }
}
