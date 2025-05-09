package org.quanyu.ai.chat.model.openai.response;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/30 20:58
 */
@Data
public class Delta {
    private String role;
    private String reasoning_content;
    private String content;
}
