package org.quanyu.ai.chat;

import java.util.List;

public interface CacheStrategy {

    List<Object> get(String sessionId);

    void set(String sessionId, List<Object> messageList);
}
