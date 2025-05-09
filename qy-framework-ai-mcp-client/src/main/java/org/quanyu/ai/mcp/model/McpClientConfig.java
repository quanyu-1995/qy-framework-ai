package org.quanyu.ai.mcp.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author quanyu
 * @date 2025/4/28 22:38
 */
@Data
@Component
@ConfigurationProperties(value = "quanyu.ai.mcp.client")
public class McpClientConfig {
    private String baseUrl;
}
