package org.quanyu.ai.mcp.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author quanyu
 * @date 2025/4/28 22:38
 */
@Data
@Component
@ConfigurationProperties(value = "quanyu.ai.mcp.client")
public class McpClientConfig {

    /**
     * 多个 MCP 服务端配置
     */
    private Map<String, String> servers;


    public String get(String serverName){
        return servers.get(serverName);
    }
}
