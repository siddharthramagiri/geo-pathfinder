package com.map.pathfinder.algorithm;

import com.map.pathfinder.model.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class PathResult {
    private List<Node> path;
    private List<Node> visitedOrder;
    private double totalDistance;
}