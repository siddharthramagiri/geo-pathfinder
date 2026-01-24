package com.map.pathfinder.service;

import com.map.pathfinder.model.Room;
import com.map.pathfinder.repository.ChatMessageRepository;
import com.map.pathfinder.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ChatService chatService;
    private final ChatMessageRepository messageRepository;

    public Room createRoom(String username) {

        Room room = new Room();
        room.setRoomId(UUID.randomUUID().toString().substring(0,6));
        room.setCreator(username);
        room.setActive(false);

        return roomRepository.save(room);
    }

    public Room joinRoom(String roomId, String username) {

        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow();

        if (username.equals(room.getCreator()) ||
                username.equals(room.getJoiner())) {
            return room;
        }

        if (room.getJoiner() == null) {
            room.setJoiner(username);
            room.setActive(true);
            Room saved = roomRepository.save(room);
            chatService.sendStatus(saved.getRoomId());
            return saved;
        }

        throw new RuntimeException("Room full");
    }

    public Room getRoom(String roomId) {
        return roomRepository.findByRoomId(roomId).orElseThrow();
    }
}
