package com.map.pathfinder.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathResponse {
    private List<double[]> finalPath;
    private List<double[]> exploration;
    private double distance; // in meters
    private int numExplored;
    private int numFinalPath;
}
