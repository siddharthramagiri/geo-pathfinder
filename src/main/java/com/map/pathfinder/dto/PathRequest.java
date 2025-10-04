package com.map.pathfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathRequest {
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private String algorithm;
}
