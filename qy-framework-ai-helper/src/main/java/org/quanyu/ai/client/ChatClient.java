package org.quanyu.ai.client;

import com.alibaba.fastjson.JSONObject;
import org.quanyu.ai.common.model.QyAIResponse;
import org.quanyu.ai.model.Config;
import org.quanyu.ai.model.ModelResponse;
import org.quanyu.ai.model.request.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @description: 抽象的对话客户端
 * @author quanyu
 * @date 2025/4/28 22:30
 */
public abstract class ChatClient {

    @Autowired
    Config config;

    public abstract QyAIResponse chat(String userInput);

    public abstract Flux<QyAIResponse> chatFlux(String userInput);

    /**
     * @description: 创建webclient客户端
     * @return: org.springframework.web.reactive.function.client.WebClient
     * @author quanyu
     * @date: 2025/5/2 11:22
     */
    protected WebClient buildWebClient(){
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(config.getChatUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if(config.getApiKey()!=null && !config.getApiKey().isEmpty()){
            builder.defaultHeader("Authorization", "Bearer " + config.getApiKey());
        }
        return builder.build();
    }

    /**
     * @description: 构建请求体
     * @author quanyu
     * @date 2025/5/2 11:23
     */
    protected RequestBody buildRequestBody(boolean stream, String userInput){
        return new RequestBody()
                .withModel(config.getModel())
                .withStream(stream)
                .addMessage("user",userInput);
    }

    protected <T extends ModelResponse> QyAIResponse chat(Class<T> clz, String userInput){
        WebClient webClient = this.buildWebClient();
        RequestBody requestBody = this.buildRequestBody(false, userInput);
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(clz)
                .block());
        T responseBody;
        try {
            responseBody = future.get();
            return responseBody.toQyAIResponse();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    protected <T extends ModelResponse> Flux<QyAIResponse> chatFlux(Class<T> clz, String userInput){
        WebClient webClient = this.buildWebClient();
        RequestBody requestBody = this.buildRequestBody(true, userInput);
        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .map(str -> {
                    if (!"[DONE]".equals(str)) {
                        T responseBody = JSONObject.parseObject(str, clz);
                        return responseBody.toQyAIResponse();
                    }
                    return QyAIResponse.getEMPTY();
                });
    }
}
