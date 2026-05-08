package com.map.pathfinder.algorithm;

import java.util.*;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import com.map.pathfinder.dto.PathResult;

public class AStarAlgorithm implements Algorithm {
    @Override
    public PathResult find(BaseGraph graph, int start, int end) {

        NodeAccess nodeAccess = graph.getNodeAccess();
        EdgeExplorer explorer = graph.createEdgeExplorer();

        Map<Integer, Double> gScore = new HashMap<>();
        Map<Integer, Double> fScore = new HashMap<>();
        Map<Integer, Integer> cameFrom = new HashMap<>();

        PriorityQueue<NodeScore> openSet =
                new PriorityQueue<>(Comparator.comparingDouble(NodeScore::fScore));

        List<Integer> visitedOrder = new ArrayList<>();
        Set<Integer> closedSet = new HashSet<>();

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, end, nodeAccess));

        openSet.add(new NodeScore(start, fScore.get(start)));

        while (!openSet.isEmpty()) {
            NodeScore current = openSet.poll();
            int currentNode = current.nodeId();

            if (closedSet.contains(currentNode)) continue;

            closedSet.add(currentNode);
            visitedOrder.add(currentNode);

            if (currentNode == end) break;

            EdgeIterator it = explorer.setBaseNode(currentNode);
            while (it.next()) {
                int neighbor = it.getAdjNode();
                double tentativeG =
                        gScore.get(currentNode) + it.getDistance();

                if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, currentNode);
                    gScore.put(neighbor, tentativeG);

                    double f =
                            tentativeG + heuristic(neighbor, end, nodeAccess);
                    fScore.put(neighbor, f);

                    openSet.add(new NodeScore(neighbor, f));
                }
            }
        }

        List<Integer> path = reconstructPath(cameFrom, start, end);

        return new PathResult(
                path,
                visitedOrder,
                gScore.getOrDefault(end, 0.0)
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


    private record NodeScore(int nodeId, double fScore) {}
}
