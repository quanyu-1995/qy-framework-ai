package org.quanyu.ai.model.openai.response;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.quanyu.ai.model.ModelResponse;
import org.quanyu.ai.common.model.QyAIResponse;

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

}
