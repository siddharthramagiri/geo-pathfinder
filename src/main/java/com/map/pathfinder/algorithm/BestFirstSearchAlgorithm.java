package com.map.pathfinder.algorithm;

import java.util.*;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import com.map.pathfinder.dto.PathResult;


public class BestFirstSearchAlgorithm {
    private record NodeHeuristic(int nodeId, double heuristic) {}

    public static PathResult find(BaseGraph graph, int start, int end) {

        NodeAccess nodeAccess = graph.getNodeAccess();
        EdgeExplorer explorer = graph.createEdgeExplorer();

        PriorityQueue<NodeHeuristic> openSet =
                new PriorityQueue<>(Comparator.comparingDouble(NodeHeuristic::heuristic));

        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> cameFrom = new HashMap<>();
        Map<Integer, Double> distance = new HashMap<>();
        List<Integer> visitedOrder = new ArrayList<>();

        openSet.add(new NodeHeuristic(
                start,
                heuristic(start, end, nodeAccess)
        ));
        distance.put(start, 0.0);

        while (!openSet.isEmpty()) {
            NodeHeuristic current = openSet.poll();
            int currentNode = current.nodeId();

            if (visited.contains(currentNode)) continue;

            visited.add(currentNode);
            visitedOrder.add(currentNode);

            if (currentNode == end) break;

            EdgeIterator it = explorer.setBaseNode(currentNode);
            while (it.next()) {
                int neighbor = it.getAdjNode();

                if (visited.contains(neighbor)) continue;

                cameFrom.putIfAbsent(neighbor, currentNode);
                distance.putIfAbsent(
                        neighbor,
                        distance.get(currentNode) + it.getDistance()
                );

                openSet.add(new NodeHeuristic(
                        neighbor,
                        heuristic(neighbor, end, nodeAccess)
                ));
            }
        }

        List<Integer> path = reconstructPath(cameFrom, start, end);

        return new PathResult(
                path,
                visitedOrder,
                distance.getOrDefault(end, 0.0)
        );
    }


    private static double heuristic(int a, int b, NodeAccess na) {
        double lat1 = Math.toRadians(na.getLat(a));
        double lon1 = Math.toRadians(na.getLon(a));
        double lat2 = Math.toRadians(na.getLat(b));
        double lon2 = Math.toRadians(na.getLon(b));

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * 6371000 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
    }

    private static List<Integer> reconstructPath(
            Map<Integer, Integer> cameFrom,
            int start,
            int end
    ) {
        LinkedList<Integer> path = new LinkedList<>();
        Integer curr = end;

        while (curr != null) {
            path.addFirst(curr);
            curr = cameFrom.get(curr);
        }

        return path.getFirst() == start ? path : List.of();
    }

}
