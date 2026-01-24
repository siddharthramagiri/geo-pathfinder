package com.map.pathfinder.service;

import com.map.pathfinder.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Location> kafkaTemplate;

    public void sendLocation(Location location) {
        kafkaTemplate.send(
                "location-room",
                location.getRoomId(),
                location
        );
    }

}
