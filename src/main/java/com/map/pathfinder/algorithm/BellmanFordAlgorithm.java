package com.map.pathfinder.algorithm;

import com.map.pathfinder.model.*;
import java.util.*;

public class BellmanFordAlgorithm {

    public static PathResult findPath(Graph graph, Node start, Node end) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        List<Node> visitedOrder = new ArrayList<>();

        // Initialize distances
        for (Node node : graph.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        visitedOrder.add(start);

        // Relax edges |V| - 1 times
        int nodeCount = graph.getNodes().size();
        for (int i = 0; i < nodeCount - 1; i++) {
            boolean updated = false;

            for (Node u : graph.getNodes()) {
                if (distances.get(u) == Double.POSITIVE_INFINITY) continue;

                for (Edge edge : graph.getNeighbors(u)) {
                    Node v = edge.getTarget();
                    double newDist = distances.get(u) + edge.getWeight();

                    if (newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        previous.put(v, u);
                        if (!visitedOrder.contains(v)) {
                            visitedOrder.add(v);
                        }
                        updated = true;
                    }
                }
            }

            if (!updated) break;
        }

        List<Node> path = reconstructPath(previous, start, end);
        return new PathResult(path, visitedOrder, distances.get(end));
    }

    private static List<Node> reconstructPath(Map<Node, Node> previous, Node start, Node end) {
        List<Node> path = new ArrayList<>();
        Node current = end;

        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        return path.isEmpty() || !path.get(0).equals(start) ? new ArrayList<>() : path;
    }
}