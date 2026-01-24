package com.map.pathfinder.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Room {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String roomId;

    private String creator;
    private String joiner;

    private boolean active;
}

