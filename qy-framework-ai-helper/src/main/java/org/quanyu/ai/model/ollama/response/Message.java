package org.quanyu.ai.model.ollama.response;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:57
 */
@Data
public class Message {
    public String role;
    public String content;
}
