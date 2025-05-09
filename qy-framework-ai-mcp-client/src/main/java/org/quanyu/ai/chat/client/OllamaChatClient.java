package org.quanyu.ai.chat.client;


import org.quanyu.ai.chat.model.QyAIResponse;
import org.quanyu.ai.chat.model.ollama.response.OllamaResponseBody;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author quanyu
 * @description: ollama 对话客户端
 * @date 2025/4/28 22:34
 */
@Component
@ConditionalOnProperty(name = "quanyu.ai.strategy", havingValue = "ollama")
public class OllamaChatClient extends ChatClient {


    public QyAIResponse chat(String userInput){
        return this.chat(OllamaResponseBody.class, userInput);
    }

    @Override
    public Flux<QyAIResponse> chatFlux(String userInput) {
        return this.chatFlux(OllamaResponseBody.class, userInput);
    }

}