package com.chatants.service;

import com.chatants.config.LocalCache;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author Samuel Mau
 */

@Service
@Component
public class ChatModelServiceImpl implements ChatModelService{

    private final OpenAiApi openAiApi;

    @Autowired
    public ChatModelServiceImpl(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    @Override
    public Flux<ChatResponse> getAnswer(String sessionId, List<Message> messages, String model) {
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .withModel(model)
                .withTemperature(0.4F)
                .withMaxTokens(200)
                .build();

        OpenAiChatModel openAiChatModel = new OpenAiChatModel(openAiApi, openAiChatOptions);
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
