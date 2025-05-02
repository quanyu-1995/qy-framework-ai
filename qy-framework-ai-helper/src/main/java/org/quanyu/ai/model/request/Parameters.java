package org.quanyu.ai.model.request;


import lombok.Data;

import java.util.List;

/**
 * @author quanyu
 * @date 2025/4/29 22:59
 */
@Data
public class Parameters {
    private String type;
    private Properties properties;
    private List<String> required;
}
