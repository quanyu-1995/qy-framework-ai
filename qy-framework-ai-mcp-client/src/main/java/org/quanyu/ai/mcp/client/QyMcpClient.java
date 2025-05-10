package org.quanyu.ai.mcp.client;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class QyMcpClient {

    @Resource
    private WebFluxSseClientTransport webFluxSseClientTransport;

    private McpSyncClient mcpClient;

    private McpSyncClient getMcpSyncClient(){
        if(mcpClient == null){
            mcpClient = McpClient.sync(webFluxSseClientTransport)
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
            mcpClient.initialize();
        }
        return mcpClient;
    }

    // 列出可用工具
    public McpSchema.ListToolsResult listTools() {
        return getMcpSyncClient().listTools();
    }

    public String callTool(String toolName, Map<String, Object> arguments){
        McpSchema.CallToolResult result = getMcpSyncClient().callTool(
                new McpSchema.CallToolRequest(toolName, arguments)
        );
        return ((McpSchema.TextContent)result.content().get(0)).text();
    }
}
