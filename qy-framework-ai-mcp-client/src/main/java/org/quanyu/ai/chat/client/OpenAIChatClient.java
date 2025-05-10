package org.quanyu.ai.chat.client;


import org.quanyu.ai.chat.CacheStrategy;
import org.quanyu.ai.chat.model.response.QyAIResponse;
import org.quanyu.ai.chat.model.response.OpenAIResponseBody;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author quanyu
 * @description: openAI 对话客户端
 * @date 2025/4/28 22:34
 */
@Component
@ConditionalOnProperty(name = "quanyu.ai.strategy", havingValue = "openai")
public class OpenAIChatClient extends ChatClient {

    @Override
    public QyAIResponse chat(CacheStrategy cacheStrategy, String sessionId, String userInput){
        return this.chat(OpenAIResponseBody.class, cacheStrategy, sessionId, userInput);
    }

    @Override
    public Flux<QyAIResponse> chatFlux(CacheStrategy cacheStrategy, String sessionId, String userInput) {
        return this.chatFlux(OpenAIResponseBody.class, cacheStrategy, sessionId, userInput);
    }

}