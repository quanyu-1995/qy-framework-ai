package org.quanyu.ai.mcp.client;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class QyMcpClient {

    @Resource
    private WebFluxSseClientTransport webFluxSseClientTransport;

    private McpSyncClient mcpSyncClient;
//    private McpAsyncClient mcpAsyncClient;

    private McpSyncClient getMcpSyncClient(){
        if(mcpSyncClient == null){
            mcpSyncClient = McpClient.sync(webFluxSseClientTransport)
                    .requestTimeout(Duration.ofSeconds(10))
                    .capabilities(McpSchema.ClientCapabilities.builder()
                            .roots(true)      // 启用根目录功能
                            .sampling()       // 启用采样功能
                            .build())
                    .sampling(request -> McpSchema.CreateMessageResult.builder()
                            .role(McpSchema.Role.ASSISTANT)
                            .message("这是一个示例响应")
                            .build())
                    .build();

            // 初始化连接
            mcpSyncClient.initialize();
        }
        return mcpSyncClient;
    }

//    private Mono<McpAsyncClient> getMcpAsyncClient(){
//        if (mcpAsyncClient == null) {
//            mcpAsyncClient = McpClient.async(webFluxSseClientTransport) // 使用异步创建方法
//                    .requestTimeout(Duration.ofSeconds(10))
//                    .capabilities(McpSchema.ClientCapabilities.builder()
//                            .roots(true)      // 启用根目录功能
//                            .sampling()       // 启用采样功能
//                            .build())
//                    .sampling(request -> Mono.just(McpSchema.CreateMessageResult.builder()
//                            .role(McpSchema.Role.ASSISTANT)
//                            .message("这是一个示例响应")
//                            .build()))
//                    .build();
//            // 返回初始化完成的 Mono 链
//            return mcpAsyncClient.initialize()
//                    .thenReturn(mcpAsyncClient)
//                    .cache();
//        }
//        return Mono.just(mcpAsyncClient);
//    }

    // 列出可用工具
    public McpSchema.ListToolsResult listTools() {
        return getMcpSyncClient().listTools();
    }

    public String callTool(String toolName, Map<String, Object> arguments) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            McpSchema.Content content = getMcpSyncClient().callTool(
                    new McpSchema.CallToolRequest(toolName, arguments)
            ).content().get(0);
            return ((McpSchema.TextContent)content).text();
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

//    public String callTool(String toolName, Map<String, Object> arguments){
//        return getMcpAsyncClient()
//                // 使用 flatMap 连接异步操作
//                .flatMap(client ->
//                        client.callTool(new McpSchema.CallToolRequest(toolName, arguments))
//                )
//                .map(result -> ((McpSchema.TextContent) result.content().get(0)).text())
//                .subscribeOn(Schedulers.boundedElastic()) // 线程隔离
//                .block(); // 在弹性线程池中阻塞
//    }
}
