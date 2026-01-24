package com.map.pathfinder.service;

import com.map.pathfinder.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "location-room", groupId = "location-group")
    public void consume(@Header(KafkaHeaders.RECEIVED_KEY) String roomId, Location location) {
        messagingTemplate.convertAndSend("/topic/location-room/" + roomId, location);
    }
}
