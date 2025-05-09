package org.quanyu.ai.mcp.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

/**
 * @author quanyu
 * @date 2025/5/6 22:39
 */
@Configuration
class McpServerBean {
    @Bean
    WebFluxSseServerTransportProvider webFluxSseServerTransportProvider() {
        return new WebFluxSseServerTransportProvider(new ObjectMapper(), "/mcp/message");
    }

    @Bean
    RouterFunction<?> mcpRouterFunction(WebFluxSseServerTransportProvider provider) {
        return provider.getRouterFunction();
    }
}