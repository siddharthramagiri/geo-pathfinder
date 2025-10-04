package com.map.pathfinder.algorithm;

import com.map.pathfinder.model.*;
import java.util.*;

public class BestFirstSearchAlgorithm {

    public static PathResult findPath(Graph graph, Node start, Node end) {
        PriorityQueue<NodeHeuristic> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(NodeHeuristic::getHeuristic)
        );
        Map<Node, Node> previous = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        List<Node> visitedOrder = new ArrayList<>();

        openSet.offer(new NodeHeuristic(start, heuristic(start, end)));

        while (!openSet.isEmpty()) {
            NodeHeuristic current = openSet.poll();
            Node currentNode = current.getNode();

            if (visited.contains(currentNode)) continue;

            visited.add(currentNode);
            visitedOrder.add(currentNode);

            if (currentNode.equals(end)) {
                break;
            }

            for (Edge edge : graph.getNeighbors(currentNode)) {
                Node neighbor = edge.getTarget();
                if (!visited.contains(neighbor)) {
                    previous.putIfAbsent(neighbor, currentNode);
                    openSet.offer(new NodeHeuristic(neighbor, heuristic(neighbor, end)));
                }
            }
        }

        List<Node> path = reconstructPath(previous, start, end);
        double distance = calculatePathDistance(graph, path);

        return new PathResult(path, visitedOrder, distance);
    }

    private static double heuristic(Node a, Node b) {
        double R = 6371000;
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

    private static double calculatePathDistance(Graph graph, List<Node> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += heuristic(path.get(i), path.get(i + 1));
        }
        return total;
    }

    private static class NodeHeuristic {
        private final Node node;
        private final double heuristic;

        public NodeHeuristic(Node node, double heuristic) {
            this.node = node;
            this.heuristic = heuristic;
        }

        public Node getNode() { return node; }
        public double getHeuristic() { return heuristic; }
    }
}