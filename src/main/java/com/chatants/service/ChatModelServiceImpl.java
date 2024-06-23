package com.chatants.service;

import com.chatants.config.LocalCache;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author Samuel Mau
 */

@Service
public class ChatModelServiceImpl implements ChatModelService{
    @Value("${mau.ai.openai.base-url}")
    private String baseUrl;
    private final String baseUrl2 = "https://mau.zeabur.app/";

    @Value("${mau.ai.openai.api-key}")
    private String apiKey;
    private final String apiKey2 = System.getenv("OPENAI_KEY");

    // TODO 这里的参数,传自定义的值能成功,但是传Value注入的值会报错
    OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);
    OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
            .withModel("gpt-3.5-turbo")
            .withTemperature(0.4F)
            .withMaxTokens(200)
            .build();

    OpenAiChatModel openAiChatModel = new OpenAiChatModel(openAiApi, openAiChatOptions);

    @Override
    public Flux<ChatResponse> getAnswer(String sessionId, List<Message> messages) {
        StringBuilder answer = new StringBuilder();
        Prompt prompt = new Prompt(messages);
        System.out.println("base url = " + baseUrl);
        System.out.println("base url2 = " + baseUrl2);
        System.out.println("api key = " + apiKey);
        System.out.println("api key2 = " + apiKey2);
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
