package com.map.pathfinder.dto;

import java.util.List;

public record PathResult(
        List<Integer> path,
        List<Integer> visited,
        double distance
) {}
