package org.quanyu.ai.chat.model.request;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:59
 */
@Data
public class Function {
    private String name;
    private String description;
    private Parameters parameters;
}
