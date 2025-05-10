package org.quanyu.ai.chat.model.request;


import io.modelcontextprotocol.spec.McpSchema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author quanyu
 * @date 2025/4/28 23:41
 */
@Data
public class RequestBody {

    private String model;
    private List<Object> messages;
    private List<Tool> tools;
    private boolean stream = false;

    public RequestBody withModel(String model){
        this.model = model;
        return this;
    }

    public RequestBody addMessage(Object message){
        if (this.messages==null){
            this.messages = new ArrayList<>();
        }
        this.messages.add(message);
        return this;
    }

    public RequestBody addTool(McpSchema.Tool mcpTool){
        if (this.tools==null){
            this.tools = new ArrayList<>();
        }
        //McpSchema.Tool 转换
        Tool tool;
        {
            McpSchema.JsonSchema inputSchema = mcpTool.inputSchema();

            Parameters parameters = new Parameters();
            parameters.setType(inputSchema.type());
            parameters.setRequired(inputSchema.required());
            parameters.setProperties(inputSchema.properties());

            Function function = new Function();
            function.setName(mcpTool.name());
            function.setDescription(mcpTool.description());
            function.setParameters(parameters);

            tool = new Tool();
            tool.setFunction(function);
            this.tools.add(tool);
        }
        return this;
    }

    public RequestBody withStream(boolean stream){
        this.stream = stream;
        return this;
    }

}
