package org.quanyu.ai.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import jakarta.annotation.Resource;
import org.quanyu.ai.mcp.model.McpClientConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class McpClientBean {

    @Resource
    private McpClientConfig mcpClientConfig;

    @Bean
    WebFluxSseClientTransport webFluxSseServerTransportProvider(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        WebClient.Builder webClientBuilderTemplate = webClientBuilderProvider
                .getIfAvailable(WebClient::builder)
                .baseUrl(mcpClientConfig.getBaseUrl());
        return new WebFluxSseClientTransport(webClientBuilderTemplate, new ObjectMapper());
    }
}
