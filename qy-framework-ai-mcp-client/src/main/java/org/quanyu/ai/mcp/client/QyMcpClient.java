package org.quanyu.ai.mcp.client;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class QyMcpClient {

    @Resource
    private Map<String, WebFluxSseClientTransport> webFluxSseClientTransportMap;

    private final Map<String, McpSyncClient> mcpSyncClients = new ConcurrentHashMap<>();

    private McpSyncClient getMcpSyncClient(String serverId) {
        return mcpSyncClients.computeIfAbsent(serverId, id -> {
            WebFluxSseClientTransport transport = webFluxSseClientTransportMap.get(id);
            if (transport == null) {
                throw new IllegalArgumentException("No MCP server found with id: " + id);
            }
            
            McpSyncClient client = McpClient.sync(transport)
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
            client.initialize();
            return client;
        });
    }

    // 列出可用工具
    public List<McpSchema.Tool> listTools() {
        List<McpSchema.Tool> tools = new ArrayList<>();
        for (String serverName : webFluxSseClientTransportMap.keySet()) {
            getMcpSyncClient(serverName)
                    .listTools()
                    .tools()
                    .forEach(tool ->
                            tools.add(new McpSchema.Tool(serverName+"__"+tool.name(), tool.description(), tool.inputSchema()))
                    );
        }
        return tools;
    }

    public String callTool(String toolName, Map<String, Object> arguments) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int index = toolName.indexOf("__");
            String serverName = toolName.substring(0, index);
            String realToolName = toolName.substring(index+2);
            McpSchema.Content content = getMcpSyncClient(serverName).callTool(
                    new McpSchema.CallToolRequest(realToolName, arguments)
            ).content().get(0);
            return ((McpSchema.TextContent)content).text();
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
