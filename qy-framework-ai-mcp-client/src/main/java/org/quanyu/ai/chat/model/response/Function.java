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
    public String name;
    public Object arguments;

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
}
