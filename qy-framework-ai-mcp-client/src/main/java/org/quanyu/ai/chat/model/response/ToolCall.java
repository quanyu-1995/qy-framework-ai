package org.quanyu.ai.chat.model.response;


import lombok.Data;

/**
 * @author quanyu
 * @date 2025/4/29 22:58
 */
@Data
public class ToolCall {
    private String type;
    private String id;
    private Function function;

    public void fluxAppend(ToolCall other) {
        if(other.type!=null){
            this.type = (this.type==null?"":this.type) + other.type;
        }
        if(other.id!=null){
            this.id = (this.id==null?"":this.id) + other.id;
        }
        this.function.fluxAppend(other.function);
    }

}
