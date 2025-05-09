package org.quanyu.ai.chat.model.request;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:59
 */
@Data
public class Tool {
    private String type = "function";
    private Function function;
}
