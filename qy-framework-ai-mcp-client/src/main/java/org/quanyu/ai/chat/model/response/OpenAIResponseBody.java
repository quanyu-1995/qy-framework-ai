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
public class OpenAIResponseBody extends ModelResponse {

    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String system_fingerprint;
    private String finish_reason;

    @Override
    public QyAIResponse toQyAIResponse() {
        QyAIResponse qyAIResponse = new QyAIResponse();

        Message message = this.getChoices().get(this.getChoices().size() - 1).getDelta();
        if(message==null){
            message = this.getChoices().get(this.getChoices().size() - 1).getMessage();
            qyAIResponse.setContent(message.getContent());
            qyAIResponse.setReasoningContent(message.getReasoning_content());
            qyAIResponse.setTotalTokens(this.getUsage().getTotal_tokens());
        }else{
            qyAIResponse.setContent(message.getContent());
            qyAIResponse.setReasoningContent(message.getReasoning_content());
        }
        return qyAIResponse;
    }

    @Override
    public List<ToolCall> toolCalls() {
        Message message = this.getChoices().get(0).getMessage()==null ?
                this.getChoices().get(0).getDelta() : this.getChoices().get(0).getMessage();
        return message.getTool_calls();
    }

    @Override
    public Message messages() {
        return this.getChoices().get(0).getMessage()==null ?
                this.getChoices().get(0).getDelta() : this.getChoices().get(0).getMessage();
    }

    @Override
    public String finishReason() {
        return this.finish_reason;
    }

    @Override
    public void fluxAppend(ModelResponse other) {
        Message messages = this.messages();
        messages.fluxAppend(other.messages());
    }
}
