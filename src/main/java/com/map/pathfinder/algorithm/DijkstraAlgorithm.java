package com.map.pathfinder.algorithm;

import com.map.pathfinder.model.*;
import java.util.*;

public class DijkstraAlgorithm {

    public static PathResult findPath(Graph graph, Node start, Node end) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(
                Comparator.comparingDouble(NodeDistance::getDistance)
        );
        List<Node> visitedOrder = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        // Initialize
        for (Node node : graph.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        queue.offer(new NodeDistance(start, 0.0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            Node currentNode = current.getNode();

            if (visited.contains(currentNode)) continue;

            visited.add(currentNode);
            visitedOrder.add(currentNode);

            if (currentNode.equals(end)) {
                break;
            }

            for (Edge edge : graph.getNeighbors(currentNode)) {
                Node neighbor = edge.getTarget();
                double newDist = distances.get(currentNode) + edge.getWeight();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentNode);
                    queue.offer(new NodeDistance(neighbor, newDist));
                }
            }
        }

        // Reconstruct path
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

    private static class NodeDistance {
        private final Node node;
        private final double distance;

        public NodeDistance(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        public Node getNode() { return node; }
        public double getDistance() { return distance; }
    }
}