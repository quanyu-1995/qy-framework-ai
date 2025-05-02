package org.quanyu.ai.model.request;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author quanyu
 * @description: 调用openAI结构请求体
 * @date 2025/4/28 23:41
 */
@Data
public class RequestBody {

    private String model;
    private List<Message> messages;
    private List<Tool> tools;
    private boolean stream = false;

    public RequestBody withModel(String model){
        this.model = model;
        return this;
    }

    public RequestBody addMessage(String role, String content){
        if (this.messages==null){
            this.messages = new ArrayList<>();
        }
        this.messages.add(new Message(role, content));
        return this;
    }

    public RequestBody addTool(Tool tool){
        if (this.tools==null){
            this.tools = new ArrayList<>();
        }
        this.tools.add(tool);
        return this;
    }

    public RequestBody withStream(boolean stream){
        this.stream = stream;
        return this;
    }

}
