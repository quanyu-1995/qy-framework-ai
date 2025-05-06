package org.quanyu.ai.mcp.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

/**
 * @author quanyu
 * @date 2025/5/6 22:39
 */
@Configuration
class McpConfig {
    @Bean
    WebFluxSseServerTransport webFluxSseServerTransport(ObjectMapper mapper) {
        return new WebFluxSseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    RouterFunction<?> mcpRouterFunction(WebFluxSseServerTransport transport) {
        return transport.getRouterFunction();
    }
}