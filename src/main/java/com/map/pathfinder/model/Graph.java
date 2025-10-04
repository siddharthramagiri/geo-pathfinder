package com.map.pathfinder.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.*;

@Data
@AllArgsConstructor
public class Graph {
    private Map<Node, List<Edge>> adjacencyList;
    private Map<String, Node> nodeMap; // added

    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.nodeMap = new HashMap<>();
    }

    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
        nodeMap.put(node.getId(), node); // add to map
    }

    public Node getNode(String id) {
        return nodeMap.get(id); // O(1)
    }

    public void addEdge(Node from, Node to, double weight) {
        adjacencyList.get(from).add(new Edge(to, weight));
        adjacencyList.get(to).add(new Edge(from, weight));
    }

    public List<Edge> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public Set<Node> getNodes() {
        return adjacencyList.keySet();
    }
}
