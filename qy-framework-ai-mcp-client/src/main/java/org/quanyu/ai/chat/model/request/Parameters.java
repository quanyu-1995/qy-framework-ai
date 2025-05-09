package org.quanyu.ai.chat.model.request;


import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author quanyu
 * @date 2025/4/29 22:59
 */
@Data
public class Parameters {
    private String type;
    private Map<String, Object> properties;
    private List<String> required;
}
