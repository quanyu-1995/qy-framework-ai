package org.quanyu.ai.chat.model.request;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:59
 */
@Data
public class Message {
    private String role;
    private String content;

    public Message(){};

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

}
