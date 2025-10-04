package com.map.pathfinder.algorithm;

import com.map.pathfinder.model.*;
import java.util.*;

public class AStarAlgorithm {

    public static PathResult findPath(Graph graph, Node start, Node end) {
        Map<Node, Double> gScore = new HashMap<>();
        Map<Node, Double> fScore = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<NodeScore> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(NodeScore::getFScore)
        );
        List<Node> visitedOrder = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        // Initialize
        for (Node node : graph.getNodes()) {
            gScore.put(node, Double.POSITIVE_INFINITY);
            fScore.put(node, Double.POSITIVE_INFINITY);
        }

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, end));
        openSet.offer(new NodeScore(start, fScore.get(start)));

        while (!openSet.isEmpty()) {
            NodeScore current = openSet.poll();
            Node currentNode = current.getNode();

            if (visited.contains(currentNode)) continue;

            visited.add(currentNode);
            visitedOrder.add(currentNode);

            if (currentNode.equals(end)) {
                break;
            }

            for (Edge edge : graph.getNeighbors(currentNode)) {
                Node neighbor = edge.getTarget();
                double tentativeGScore = gScore.get(currentNode) + edge.getWeight();

                if (tentativeGScore < gScore.get(neighbor)) {
                    previous.put(neighbor, currentNode);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, end));
                    openSet.offer(new NodeScore(neighbor, fScore.get(neighbor)));
                }
            }
        }

        List<Node> path = reconstructPath(previous, start, end);
        return new PathResult(path, visitedOrder, gScore.get(end));
    }

    private static double heuristic(Node a, Node b) {
        // Haversine distance
        double R = 6371000; // Earth radius in meters
        double lat1 = Math.toRadians(a.getLat());
        double lat2 = Math.toRadians(b.getLat());
        double dLat = Math.toRadians(b.getLat() - a.getLat());
        double dLon = Math.toRadians(b.getLon() - a.getLon());

        double hav = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(hav), Math.sqrt(1 - hav));
        return R * c;
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

    private static class NodeScore {
        private final Node node;
        private final double fScore;

        public NodeScore(Node node, double fScore) {
            this.node = node;
            this.fScore = fScore;
        }

        public Node getNode() { return node; }
        public double getFScore() { return fScore; }
    }
}