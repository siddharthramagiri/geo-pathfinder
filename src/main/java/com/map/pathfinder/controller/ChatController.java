package com.map.pathfinder.controller;


import com.map.pathfinder.model.ChatMessage;
import com.map.pathfinder.model.Location;
import com.map.pathfinder.service.ChatService;
import com.map.pathfinder.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final KafkaProducerService producer;

    @MessageMapping("/send.message")
    public void sendMessage(ChatMessage message) {
        System.out.println(message.getUsername() + " : " + message.getMessage());
        chatService.sendMessage(message);
    }

    @MessageMapping("/send.location")
    public void sendLocation(Location location) {
//        System.out.println(location.toString());
//        chatService.sendLocation(location);
        producer.sendLocation(location);
    }
}
