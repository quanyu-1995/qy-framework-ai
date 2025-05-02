package org.quanyu.ai.model;


import org.quanyu.ai.common.model.QyAIResponse;

/**
 * @author quanyu
 * @description: 抽象的外部模型調用返回结构体
 * @date 2025/4/29 22:21
 */
public abstract class ModelResponse {

    public abstract QyAIResponse toQyAIResponse();
}
