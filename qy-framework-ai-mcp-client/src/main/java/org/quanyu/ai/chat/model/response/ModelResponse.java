package org.quanyu.ai.chat.model.response;


import java.util.List;

/**
 * @author quanyu
 * @date 2025/4/29 22:21
 */
public abstract class ModelResponse {

    public abstract QyAIResponse toQyAIResponse();

    public abstract List<ToolCall> toolCalls();

    public abstract Object messages();
}
