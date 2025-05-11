package org.quanyu.ai.chat.model.response;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:57
 */
@Data
public class Choice {
    private int index;
    private Message message;
    private String finish_reason;
    private Message delta;
}
