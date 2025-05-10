package org.quanyu.test;


import org.quanyu.ai.chat.client.ChatClient;
import org.quanyu.ai.chat.model.response.QyAIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author quanyu
 * @date 2025/4/30 22:02
 */
@RestController
public class TestController {

    @Autowired
    ChatClient chatClient;

    @GetMapping("chat")
    public QyAIResponse chat(@RequestParam(value = "sessionId", required = false) String sessionId, @RequestParam(value = "userInput") String userInput){
        return chatClient.chat(sessionId, userInput);
    }

    @GetMapping(value = "chatFlux", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<QyAIResponse> chatFlux(@RequestParam(value = "sessionId", required = false) String sessionId, @RequestParam(value = "userInput") String userInput){
        return chatClient.chatFlux(sessionId, userInput);
    }

}
