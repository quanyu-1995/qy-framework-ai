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

    @Override
    public QyAIResponse toQyAIResponse() {
        QyAIResponse qyAIResponse = new QyAIResponse();

        Delta delta = this.getChoices().get(this.getChoices().size() - 1).getDelta();
        if(delta==null){
            Message message = this.getChoices().get(this.getChoices().size() - 1).getMessage();
            qyAIResponse.setContent(message.getContent());
            qyAIResponse.setReasoningContent(message.getReasoning_content());
            qyAIResponse.setTotalTokens(this.getUsage().getTotal_tokens());
        }else{
            qyAIResponse.setContent(delta.getContent());
            qyAIResponse.setReasoningContent(delta.getReasoning_content());
        }
        return qyAIResponse;
    }

    @Override
    public List<ToolCall> toolCalls() {
        return this.getChoices().get(0).getMessage().getTool_calls();
    }

    @Override
    public Object messages() {
        return this.getChoices().get(0).getMessage();
    }

}
