package org.quanyu.ai.chat.model.openai.response;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:58
 */
@Data
public class Function {
    public String name;
    public String arguments;
}
