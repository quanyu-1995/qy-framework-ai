package org.quanyu.ai.client;


import org.quanyu.ai.common.model.QyAIResponse;
import org.quanyu.ai.model.openai.response.OpenAIResponseBody;
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

    public QyAIResponse chat(String userInput){
        return this.chat(OpenAIResponseBody.class, userInput);
    }

    @Override
    public Flux<QyAIResponse> chatFlux(String userInput) {
        return this.chatFlux(OpenAIResponseBody.class, userInput);
    }

}