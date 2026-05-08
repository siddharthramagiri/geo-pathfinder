package com.map.pathfinder.algorithm;

import java.util.Map;

public class AlgorithmFactory {
    private static final Map<String, Algorithm> algorithms = Map.of(
            "astar", new AStarAlgorithm(),
            "dijkstra", new DijkstraAlgorithm(),
            "best", new BestFirstSearchAlgorithm()
    );

    public static Algorithm getAlgorithm(String name) {
        Algorithm algo = algorithms.get(name.toLowerCase());
        if (algo == null) {
            throw new IllegalArgumentException("Unknown algorithm: " + name);
        }
        return algo;
    }
}