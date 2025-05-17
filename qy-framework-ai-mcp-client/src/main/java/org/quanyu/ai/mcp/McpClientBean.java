package org.quanyu.ai.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import jakarta.annotation.Resource;
import org.quanyu.ai.mcp.model.McpClientConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class McpClientBean {

    @Resource
    private McpClientConfig mcpClientConfig;

    @Bean
    Map<String, WebFluxSseClientTransport> webFluxSseClientTransports(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        Map<String, WebFluxSseClientTransport> transports = new HashMap<>();

        // 添加其他配置的 transport
        mcpClientConfig.getServers().forEach((key, url) -> {
            WebClient.Builder webClientBuilderTemplate = webClientBuilderProvider
                    .getIfAvailable(WebClient::builder)
                    .baseUrl(url);

            transports.put(key, new WebFluxSseClientTransport(webClientBuilderTemplate, new ObjectMapper()));
        });
        
        return transports;
    }
}
