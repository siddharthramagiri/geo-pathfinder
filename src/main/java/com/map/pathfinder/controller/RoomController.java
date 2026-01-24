package com.map.pathfinder.controller;

import com.map.pathfinder.dto.roomDto.CreateRoomRequest;
import com.map.pathfinder.dto.roomDto.JoinRoomRequest;
import com.map.pathfinder.model.ChatMessage;
import com.map.pathfinder.model.Room;
import com.map.pathfinder.service.ChatService;
import com.map.pathfinder.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;
    private final ChatService chatService;

    @PostMapping("/create")
    public Room create(@RequestBody CreateRoomRequest req) {
        return roomService.createRoom(req.getUsername());
    }

    @PostMapping("/join")
    public Room join(@RequestBody JoinRoomRequest req) {
        return roomService.joinRoom(req.getRoomId(), req.getUsername());
    }

    @GetMapping("/{roomId}")
    public Room status(@PathVariable String roomId) {
        return roomService.getRoom(roomId);
    }

    @GetMapping("/{roomId}/messages")
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        return chatService.getMessages(roomId);
    }
}
