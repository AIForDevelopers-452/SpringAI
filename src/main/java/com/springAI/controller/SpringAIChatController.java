package com.springAI.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class SpringAIChatController {
    private final ChatClient chatClient;
    private final ChatModel model;

    public SpringAIChatController(ChatClient.Builder builder, ChatModel model) {
        this.chatClient = builder.build();
        this.model = model;
    }
    //chat client you can think of the easy way to communicate with models(llm)
    //is the engine of the car
    //higher level abstraction
    @GetMapping("chat-client/chat")
    public String chat_client(@RequestParam String message) {
        return chatClient
                .prompt()
                .system("You are a teacher explaining concepts to beginners.\n" +
                "Always explain using simple language and a real-life example.\n" +
                "Avoid technical jargon.")
                .user(message)
                .call()
                .content();
    }

    //the chat model is doing the actual communication
    //is the steering wheel to control the engine
    //lower level abstraction
    @GetMapping("chat-model/chat")
    public String chat_model(@RequestParam String message) {
        return model.call(message);
    }

    @GetMapping(value = "/chat-model/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return model
                .stream("provide short description." + message);
    }

    @GetMapping(value = "/chat-client/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String message) {
        return chatClient
                .prompt()
                .system("You are a teacher explaining concepts to beginners.\n" +
                        "Always explain using simple language and a real-life example.\n" +
                        "Avoid technical jargon.")
                .user("provide short description."+message)
                .stream()
                .content();
    }

}

