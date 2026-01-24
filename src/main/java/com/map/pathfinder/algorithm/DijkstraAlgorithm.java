package com.map.pathfinder.algorithm;

import com.graphhopper.storage.BaseGraph;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;
import com.map.pathfinder.dto.PathResult;

import java.util.*;

public class DijkstraAlgorithm {
    public static PathResult find(BaseGraph graph, int start, int end) {

        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        List<Integer> visitedOrder = new ArrayList<>();

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        pq.add(new int[]{start, 0});
        dist.put(start, 0.0);

        EdgeExplorer explorer = graph.createEdgeExplorer();

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int node = curr[0];

            if (visitedOrder.contains(node)) continue;
            visitedOrder.add(node);

            if (node == end) break;

            EdgeIterator it = explorer.setBaseNode(node);
            while (it.next()) {
                int next = it.getAdjNode();
                double newDist = dist.get(node) + it.getDistance();

                if (newDist < dist.getOrDefault(next, Double.MAX_VALUE)) {
                    dist.put(next, newDist);
                    parent.put(next, node);
                    pq.add(new int[]{next, (int) newDist});
                }
            }
        }

        List<Integer> path = reconstructPath(parent, start, end);
        return new PathResult(path, visitedOrder, dist.getOrDefault(end, 0.0));
    }

    private static List<Integer> reconstructPath(Map<Integer, Integer> parent, int start, int end) {
        LinkedList<Integer> path = new LinkedList<>();
        Integer curr = end;

        while (curr != null) {
            path.addFirst(curr);
            curr = parent.get(curr);
        }

        return path.getFirst() == start ? path : List.of();
    }
}
