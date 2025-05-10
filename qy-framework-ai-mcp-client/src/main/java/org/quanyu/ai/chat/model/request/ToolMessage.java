package org.quanyu.ai.chat.model.request;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author quanyu
 * @date 2025/5/10 22:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ToolMessage extends Message{
    private String tool_call_id;

    public ToolMessage(String role, String content) {
        super(role, content);
    }

    public ToolMessage(String role, String toolId, String content) {
        this.setRole(role);
        this.setTool_call_id(toolId);
        this.setContent(content);
    }
}
