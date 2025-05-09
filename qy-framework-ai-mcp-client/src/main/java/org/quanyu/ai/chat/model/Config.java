package org.quanyu.ai.chat.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author quanyu
 * @description: openAI配置文件
 * @date 2025/4/28 22:38
 */
@Data
@Component
@ConfigurationProperties(value = "quanyu.ai.config")
public class Config {
    private String chatUrl;
    private String apiKey;
    private String model;
}
