package com.map.pathfinder.service;

import com.map.pathfinder.model.ChatMessage;
import com.map.pathfinder.model.Location;
import com.map.pathfinder.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(ChatMessage message) {
        ChatMessage msg = chatMessageRepository.save(message);
        messagingTemplate.convertAndSend(
                "/topic/chat-room/" + message.getRoomId(), msg);
    }

    public void sendStatus(String roomId) {
        messagingTemplate.convertAndSend("/topic/room-status/" + roomId, "ACTIVE");
    }

    public List<ChatMessage> getMessages(String roomId) {
        List<ChatMessage> messages = chatMessageRepository.findAllByRoomIdOrderByTimeStamp(roomId);
        return messages;
    }

    public void sendLocation(Location location) {
        messagingTemplate.convertAndSend("/topic/location-room/" + location.getRoomId(), location);
    }
}
