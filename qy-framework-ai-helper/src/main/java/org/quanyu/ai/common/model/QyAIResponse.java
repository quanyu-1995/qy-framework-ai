package org.quanyu.ai.common.model;


import lombok.Data;
import lombok.Getter;

/**
 * @author quanyu
 * @description: qyAI对话返回值结构体
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
