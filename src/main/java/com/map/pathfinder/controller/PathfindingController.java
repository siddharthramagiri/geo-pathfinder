package com.map.pathfinder.controller;

import com.map.pathfinder.dto.*;
import com.map.pathfinder.service.PathfindingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pathfinding")
@RequiredArgsConstructor
@Slf4j
public class PathfindingController {

    private final PathfindingService pathfindingService;

    @PostMapping("/calculate")
    public ResponseEntity<PathResponse> calculatePath(@RequestBody PathRequest request) {
        try {
            log.info("Received path calculation request: {}", request);
            PathResponse response = pathfindingService.calculatePath(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error calculating path", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Pathfinding service is running");
    }
}