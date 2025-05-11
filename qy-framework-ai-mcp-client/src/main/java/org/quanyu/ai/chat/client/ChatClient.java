package org.quanyu.ai.chat.client;

import com.alibaba.fastjson.JSONObject;
import io.modelcontextprotocol.spec.McpSchema;
import org.quanyu.ai.chat.CacheStrategy;
import org.quanyu.ai.chat.model.Config;
import org.quanyu.ai.chat.model.response.ModelResponse;
import org.quanyu.ai.chat.model.response.QyAIResponse;
import org.quanyu.ai.chat.model.request.RequestBody;
import org.quanyu.ai.chat.model.response.ToolCall;
import org.quanyu.ai.mcp.client.QyMcpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

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
        return chat(null, sessionId, userInput);
    }

    public Flux<QyAIResponse> chatFlux(String sessionId, String userInput){
        return chatFlux(null, sessionId, userInput);
    }

    protected <T extends ModelResponse> QyAIResponse chat(Class<T> clz, CacheStrategy cacheStrategy, String sessionId, String userInput) {
        WebClient webClient = this.buildWebClient();
        RequestBody requestBody = this.buildRequestBody(false, cacheStrategy, sessionId, userInput);
        List<Object> messages = requestBody.getMessages();

        T modelResponse = chatModel(clz, webClient, requestBody);
        List<ToolCall> toolCalls = modelResponse.toolCalls();
        while(toolCalls!=null && !toolCalls.isEmpty()){
            ToolCall toolCall = toolCalls.get(0);
            messages.add(modelResponse.messages());
            String toolContent = qyMcpClient.callTool(toolCall.getFunction().getName(), toolCall.getFunction().getArgumentsMap());
            JSONObject message = new JSONObject();
            message.put("role","tool");
            message.put("content", toolContent);
            message.put("tool_call_id", toolCall.getId());
            messages.add(message);
            modelResponse = chatModel(clz, webClient, requestBody);
            messages.add(modelResponse.messages());

            toolCalls = modelResponse.toolCalls();
        }
        messages.add(modelResponse.messages());
        this.cacheMessage(cacheStrategy, sessionId, messages);

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
        return chatFlux(clz, webClient, requestBody);
    }


    protected Flux<QyAIResponse> chatFlux(Class<? extends ModelResponse> clz,
                                               WebClient webClient,
                                               RequestBody requestBody) {
        AtomicReference<ModelResponse> atomicMr = new AtomicReference<>();

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .concatMap(str -> {
                    if ("[DONE]".equals(str)) {
                        return Flux.just(QyAIResponse.getEMPTY());
                    }
                    ModelResponse newMr = JSONObject.parseObject(str, clz);

                    ModelResponse mr = atomicMr.get();
                    if(mr==null){
                        mr = newMr;
                    }else {
                        mr.fluxAppend(newMr);
                    }
                    atomicMr.set(mr);

                    return Flux.just(newMr.toQyAIResponse());
                })
                .concatWith(
                        Flux.defer(() -> {
                            ModelResponse mr = atomicMr.get();
                            ToolCall toolCall;
                            if (mr.toolCalls() !=null && !mr.toolCalls().isEmpty() && (toolCall = mr.toolCalls().get(0)) != null) {
                                //处理工具调用
                                String toolContent = qyMcpClient.callTool(toolCall.getFunction().getName(), toolCall.getFunction().getArgumentsMap());
                                JSONObject message = new JSONObject();
                                message.put("role","tool");
                                message.put("content", toolContent);
                                message.put("tool_call_id", toolCall.getId());
                                requestBody
                                        .addMessage(mr.messages())
                                        .addMessage(message);
                                return chatFlux(clz, webClient, requestBody);
                            } else {
                                return Flux.empty();
                            }
                        })
                );
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
        List<Object> messageList = new ArrayList<>();
        if(cacheStrategy!=null){
            messageList = cacheStrategy.get(sessionId);
        }
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

    protected void cacheMessage(CacheStrategy cacheStrategy, String sessionId, List<Object> messageList){
        if(cacheStrategy!=null){
            cacheStrategy.set(sessionId, messageList);
        }
    }
}
