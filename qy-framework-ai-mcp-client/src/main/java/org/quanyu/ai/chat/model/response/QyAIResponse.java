package org.quanyu.ai.chat.model.response;


import lombok.Data;
import lombok.Getter;
import org.quanyu.ai.chat.model.request.Tool;

/**
 * @author quanyu
 * @date 2025/4/29 21:48
 */
@Data
public class QyAIResponse {

    @Getter
    private static QyAIResponse EMPTY = new QyAIResponse();

    private String reasoningContent;
    private String content;
    private Integer totalTokens;
}
