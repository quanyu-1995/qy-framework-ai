package org.quanyu.ai.model.ollama.response;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.quanyu.ai.common.model.QyAIResponse;
import org.quanyu.ai.model.ModelResponse;

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
}