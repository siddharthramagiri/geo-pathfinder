package com.map.pathfinder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Entity
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;
    private String username;

    private double latitude;
    private double longitude;

    private LocalDateTime timeStamp = LocalDateTime.now();
}
