
package org.quanyu.ai.mcp.server;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;

@Component
public class McpServerRegistrar {
    
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private WebFluxSseServerTransport transport;

    private static final SchemaGenerator SUBTYPE_SCHEMA_GENERATOR;

    static {
        Module jacksonModule = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED);
        SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder = (new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)).with(jacksonModule).with(Option.EXTRA_OPEN_API_FORMAT_VALUES, new Option[0]).with(Option.PLAIN_DEFINITION_KEYS, new Option[0]);
        SchemaGeneratorConfig subtypeSchemaGeneratorConfig = schemaGeneratorConfigBuilder.without(Option.SCHEMA_VERSION_INDICATOR, new Option[0]).build();
        SUBTYPE_SCHEMA_GENERATOR = new SchemaGenerator(subtypeSchemaGeneratorConfig);
    }

    @PostConstruct
    public void registerMcpServers() {
        // 创建具有自定义配置的服务器
        McpSyncServer syncServer = McpServer.sync(transport)
                .serverInfo("qy-mcp-server", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .resources(true, true)     // 启用资源支持
                        .tools(true)         // 启用工具支持
                        .prompts(true)       // 启用提示支持
                        .logging()           // 启用日志支持
                        .build())
                .build();
        // 查找方法级别的Tool注解
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    Tool toolAnnotation = method.getAnnotation(Tool.class);
                    // 处理参数
                    ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
                    schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
                    schema.put("type","object");
                    schema.put("additionalProperties", false);
                    ObjectNode properties = schema.putObject("properties");
                    ArrayNode required = properties.putArray("required");

                    Parameter[] parameters = method.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        if(parameters[i].isAnnotationPresent(McpParameter.class)){
                            String name = parameters[i].getName();
                            Type parameterType = method.getGenericParameterTypes()[i];
                            McpParameter mcpParameter = parameters[i].getAnnotation(McpParameter.class);
                            ObjectNode parameterNode = SUBTYPE_SCHEMA_GENERATOR.generateSchema(parameterType);
                            parameterNode.put("description", mcpParameter.desc());
                            properties.set(name, parameterNode);
                            if(mcpParameter.required()){
                                required.add(name);
                            }
                        }
                    }
                    // 注册方法级别的服务

                    McpServerFeatures.SyncToolRegistration syncToolRegistration = buildSyncToolRegistration(method.getName(), toolAnnotation.desc(), schema.toPrettyString());
                    syncServer.addTool(syncToolRegistration);
                }
            }
        }
        // 发送日志通知
        syncServer.loggingNotification(McpSchema.LoggingMessageNotification.builder()
                .level(McpSchema.LoggingLevel.INFO)
                .logger("custom-logger")
                .data("服务器已初始化")
                .build());
    }

    private McpServerFeatures.SyncToolRegistration buildSyncToolRegistration(String toolName, String toolDesc, String schema){
        // 定义Tool
        McpSchema.Tool tool = new McpSchema.Tool(
                toolName,
                toolDesc,
                schema
        );
        // 定义同步工具
        return new McpServerFeatures.SyncToolRegistration(
                tool,
                request -> new McpSchema.CallToolResult(new ArrayList<>(), false)
        );
    }
}