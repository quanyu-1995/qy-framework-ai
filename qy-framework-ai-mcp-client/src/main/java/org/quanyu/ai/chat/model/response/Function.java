package org.quanyu.ai.chat.model.response;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quanyu
 * @date 2025/4/29 22:58
 */
@Data
public class Function {
    private String name;
    private Object arguments;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getArgumentsMap(){
        if(arguments instanceof Map){
            return (Map<String, Object>)arguments;
        } else if(arguments instanceof  String){
            return JSONObject.parseObject((String)arguments);
        } else {
            String jsonString = JSONObject.toJSONString(arguments);
            if(jsonString.isBlank()){
                return new HashMap<>();
            }
            return JSONObject.parseObject(jsonString);
        }
    }

    public void fluxAppend(Function other){
        if(other.name != null){
            this.name = other.name;
        }
        if(arguments instanceof Map){
            // TODO
        } else if(arguments instanceof  String){
            if(other.arguments != null){
                this.arguments += (String)other.arguments;
            }
        } else {
            // TODO
        }
    }
}
