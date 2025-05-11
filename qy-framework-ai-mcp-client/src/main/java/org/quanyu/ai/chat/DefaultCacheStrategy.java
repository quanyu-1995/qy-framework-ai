package org.quanyu.ai.chat;


import com.alibaba.fastjson.JSON;
import org.quanyu.ai.SpringBeanContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author quanyu
 * @date 2025/5/10 18:23
 */
public class DefaultCacheStrategy implements CacheStrategy{

    private final StringRedisTemplate redisTemplate = new SpringBeanContext().getBean(StringRedisTemplate.class);

    public List<Object> get(String sessionId){
        List<Object> messageList = new ArrayList<>();
        if (sessionId==null || sessionId.isBlank()){
            return messageList;
        }
        String json = redisTemplate.opsForValue().get(sessionId);
        if(json==null || json.isBlank()){
            return messageList;
        }
        return JSON.parseArray(json);
    }

    @Override
    public void set(String sessionId, List<Object> messageList) {
        if (sessionId==null || sessionId.isBlank()){
            return;
        }
        redisTemplate.opsForValue().set(
                sessionId,
                JSON.toJSONString(messageList)
        );
    }

}
