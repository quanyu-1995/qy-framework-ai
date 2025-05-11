package org.quanyu.ai.chat.model.response;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author quanyu
 * @date 2025/4/29 22:57
 */
@Data
public class Message {
    private String role;
    private String content;
    private String reasoning_content;
    private List<ToolCall> tool_calls;

    public void fluxAppend(Message other){
        if(other.role!=null){
            this.role += other.role;
        }
        if(other.content!=null){
            this.content += other.content;
        }
        if(other.reasoning_content!=null){
            this.reasoning_content += other.reasoning_content;
        }
        if(other.role!=null){
            this.role = other.role;
        }

        if(other.getTool_calls()!=null && !other.getTool_calls().isEmpty()){
            if(this.tool_calls!=null && !this.tool_calls.isEmpty()){
                this.tool_calls.get(0).fluxAppend(other.getTool_calls().get(0));
            }else{
                this.tool_calls = other.getTool_calls();
            }
        }
    }
}
