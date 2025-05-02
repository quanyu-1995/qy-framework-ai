package org.quanyu.ai.model.request;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:59
 */
@Data
public class Tool {
    private String type;
    private Function function;
}
