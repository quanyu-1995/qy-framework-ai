package org.quanyu.ai.chat.model.response;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:58
 */
@Data
public class ToolCall {
    private String type;
    private String id;
    private Function function;
}
