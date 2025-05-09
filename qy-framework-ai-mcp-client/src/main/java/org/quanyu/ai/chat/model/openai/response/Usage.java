package org.quanyu.ai.chat.model.openai.response;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:58
 */
@Data
public class Usage {
    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;
}
