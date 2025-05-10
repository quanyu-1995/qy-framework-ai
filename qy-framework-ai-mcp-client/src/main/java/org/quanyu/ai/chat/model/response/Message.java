package org.quanyu.ai.chat.model.response;


import lombok.Data;

import java.util.List;

/**
 * @author quanyu
 * @date 2025/4/29 22:57
 */
@Data
public class Message {
    public String role;
    public String content;
    public String reasoning_content;
    public List<ToolCall> tool_calls;
}
