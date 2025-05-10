package org.quanyu.ai.chat.client;

import com.alibaba.fastjson.JSONObject;
import io.modelcontextprotocol.spec.McpSchema;
import org.quanyu.ai.chat.CacheStrategy;
import org.quanyu.ai.chat.DefaultCacheStrategy;
import org.quanyu.ai.chat.model.Config;
import org.quanyu.ai.chat.model.response.ModelResponse;
import org.quanyu.ai.chat.model.response.QyAIResponse;
import org.quanyu.ai.chat.model.request.Message;
import org.quanyu.ai.chat.model.request.RequestBody;
import org.quanyu.ai.chat.model.response.ToolCall;
import org.quanyu.ai.mcp.client.QyMcpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
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
    @Autowired
    QyMcpClient qyMcpClient;

    public abstract QyAIResponse chat(CacheStrategy cacheStrategy, String sessionId, String userInput);

    public abstract Flux<QyAIResponse> chatFlux(CacheStrategy cacheStrategy, String sessionId, String userInput);

    public QyAIResponse chat(String sessionId, String userInput){
        return chat(new DefaultCacheStrategy(), sessionId, userInput);
    }

    public Flux<QyAIResponse> chatFlux(String sessionId, String userInput){
        return chatFlux(new DefaultCacheStrategy(), sessionId, userInput);
    }

    protected <T extends ModelResponse> QyAIResponse chat(Class<T> clz, CacheStrategy cacheStrategy, String sessionId, String userInput) {
        WebClient webClient = this.buildWebClient();
        RequestBody requestBody = this.buildRequestBody(false, cacheStrategy, sessionId, userInput);
        T modelResponse = chatModel(clz, webClient, requestBody);
        List<ToolCall> toolCalls = modelResponse.toolCalls();
        if(toolCalls!=null && !toolCalls.isEmpty()){
            ToolCall toolCall = toolCalls.get(0);
            requestBody.addMessage(modelResponse.messages());
            String toolContent = qyMcpClient.callTool(toolCall.getFunction().getName(), toolCall.getFunction().getArgumentsMap());
            JSONObject message = new JSONObject();
            message.put("role","tool");
            message.put("content", toolContent);
            message.put("tool_call_id", toolCall.getId());
            requestBody.addMessage(message);
            modelResponse = chatModel(clz, webClient, requestBody);
        }
        return modelResponse.toQyAIResponse();
    }

    protected <T extends ModelResponse> T chatModel(Class<T> clz, WebClient webClient, RequestBody requestBody) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(clz)
                .block());
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T extends ModelResponse> Flux<QyAIResponse> chatFlux(Class<T> clz, CacheStrategy cacheStrategy, String sessionId, String userInput){
        WebClient webClient = this.buildWebClient();
        RequestBody requestBody = this.buildRequestBody(true, cacheStrategy, sessionId, userInput);
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
    protected RequestBody buildRequestBody(boolean stream, CacheStrategy cacheStrategy, String sessionId, String userInput){
        RequestBody requestBody = new RequestBody()
                .withModel(config.getModel())
                .withStream(stream);

        List<Object> messageList = cacheStrategy.get(sessionId);
        JSONObject message = new JSONObject();
        message.put("role","user");
        message.put("content", userInput);
        messageList.add(message);
        requestBody.setMessages(messageList);

        List<McpSchema.Tool> tools = qyMcpClient.listTools().tools();
        if(tools!=null && !tools.isEmpty()){
            tools.forEach(requestBody::addTool);
        }
        return requestBody;
    }

}
