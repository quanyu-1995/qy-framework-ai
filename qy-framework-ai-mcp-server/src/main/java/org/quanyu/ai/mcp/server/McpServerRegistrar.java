
package org.quanyu.ai.mcp.server;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Component
public class McpServerRegistrar {
    
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private WebFluxSseServerTransportProvider transport;

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
            // 跳过自身 Bean，避免循环依赖
            if (beanName.equals("mcpServerRegistrar")) {
                continue;
            }
            Object bean = applicationContext.getBean(beanName);
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    Tool toolAnnotation = method.getAnnotation(Tool.class);
                    // 处理参数
                    ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
                    schema.put("type","object");
                    ArrayNode required = schema.putArray("required");
                    Parameter[] parameters = method.getParameters();
                    for (Parameter parameter : parameters) {
                        this.putProperties(schema, parameter);
                        required.add(parameter.getName());
                    }
                    McpServerFeatures.SyncToolSpecification syncToolSpecification = buildSyncToolSpecification(bean, method, toolAnnotation.desc(), schema.toPrettyString());
                    syncServer.addTool(syncToolSpecification);
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

    private void putProperties(ObjectNode schema, Parameter parameter){
        // 处理参数
        String typeName = jsonTypeMapper(parameter);

        ObjectNode properties = (ObjectNode)schema.get("properties");
        if(properties==null){
            properties = schema.putObject("properties");
        }
        ObjectNode paramSchema = properties.putObject(parameter.getName());
        paramSchema.put("type", typeName);
        if(parameter.isAnnotationPresent(McpParameter.class)){
            McpParameter mcpParameter = parameter.getAnnotation(McpParameter.class);
            paramSchema.put("description", mcpParameter.desc());
        }
    }

    private String jsonTypeMapper(Parameter parameter){
        Class<?> paramType = parameter.getType();
        Type genericType = parameter.getParameterizedType();

        // 基础类型判断
        if (paramType.isPrimitive()) {
            if (paramType == boolean.class) return "boolean";
            else if (paramType == char.class) return "string";
            else return "number";
        } else if (Number.class.isAssignableFrom(paramType)) {
            return "number";
        } else if (paramType == String.class) {
            return "string";
        } else if (paramType == Boolean.class) {
            return "boolean";
        }

        // 复合类型判断
        if (paramType.isArray() || Collection.class.isAssignableFrom(paramType)) {
            return "array";
        } else if (Map.class.isAssignableFrom(paramType)) {
            return "object";
        }

        // 泛型类型处理
        if (genericType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) genericType).getRawType();
            if (rawType == List.class || rawType == Set.class) {
                return "array";
            }
        }

        // 默认视为对象
        return "object";
    }

    private McpServerFeatures.SyncToolSpecification buildSyncToolSpecification(Object instance, Method method, String toolDesc, String schema){
        // 定义Tool
        McpSchema.Tool tool = new McpSchema.Tool(
                method.getName(),
                toolDesc,
                schema
        );
        // 定义同步工具
        return new McpServerFeatures.SyncToolSpecification(
                tool,
                (exchange, arguments) -> {
                    //执行函数
                    try {
                        Parameter[] parameters = method.getParameters();
                        Object[] argArr = new Object[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            argArr[i] = arguments.get(parameters[i].getName());
                        }
                        Object obj = method.invoke(instance, argArr);
                        String str = obj==null?"": JSON.toJSONString(obj);
                        return new McpSchema.CallToolResult(Collections.singletonList(new McpSchema.TextContent(str)), false);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}