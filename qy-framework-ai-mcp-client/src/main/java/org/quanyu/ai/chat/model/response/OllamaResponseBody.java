package org.quanyu.ai.chat.model.response;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author quanyu
 * @date 2025/4/29 21:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OllamaResponseBody extends ModelResponse {

    private String model;
    private Message message;

    @Override
    public QyAIResponse toQyAIResponse() {
        QyAIResponse qyAIResponse = new QyAIResponse();
        if(message!=null){
            qyAIResponse.setContent(message.getContent());
        }
        return qyAIResponse;
    }

    @Override
    public List<ToolCall> toolCalls() {
        return this.getMessage().getTool_calls();
    }

    @Override
    public Message messages() {
        return this.getMessage();
    }

    @Override
    public String finishReason() {
        return "";
    }

    @Override
    public void fluxAppend(ModelResponse other) {
        OllamaResponseBody orb = (OllamaResponseBody)other;
        this.model += orb.getModel();
        this.message.fluxAppend(((OllamaResponseBody) other).getMessage());
    }
}