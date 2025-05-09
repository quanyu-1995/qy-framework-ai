package org.quanyu.ai.chat.model;


import lombok.Data;
import lombok.Getter;

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
