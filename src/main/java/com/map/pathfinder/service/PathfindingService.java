package com.map.pathfinder.service;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeIterator;
import com.map.pathfinder.algorithm.*;
import com.map.pathfinder.dto.*;
import com.map.pathfinder.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.io.File;


@Slf4j
@Service
public class PathfindingService {

    private GraphHopper hopper;
    private Graph customGraph;

    @PostConstruct
    public void init() {
        hopper = new GraphHopper();
        hopper.setOSMFile(new File("src/main/resources/data/telangana-latest.osm.pbf").getAbsolutePath());
        hopper.setGraphHopperLocation("graph-cache");

        Profile carProfile = new Profile("car").setVehicle("car").setWeighting("custom");
        hopper.setProfiles(carProfile);

        hopper.importOrLoad();
        log.info("GraphHopper initialized successfully\nBuilding Custom Graph");

        // Convert GraphHopper graph into our custom Graph
        this.customGraph = buildCustomGraph(hopper);
        log.info("Custom Graph built with {} nodes", customGraph.getNodes().size());
    }

    private Graph buildCustomGraph(GraphHopper hopper) {
        Graph graph = new Graph();
        BaseGraph baseGraph = (BaseGraph) hopper.getBaseGraph();
        NodeAccess nodeAccess = baseGraph.getNodeAccess();
        log.info("================================== BEFORE BUILDING CUSTOM GRAPH ==========================================");

        for (int i = 0; i < baseGraph.getNodes(); i++) {
            Node node = new Node(String.valueOf(i), nodeAccess.getLat(i), nodeAccess.getLon(i));
            graph.addNode(node);
        }

        EdgeIterator iter;
        for (int nodeId = 0; nodeId < baseGraph.getNodes(); nodeId++) {
            iter = baseGraph.createEdgeExplorer().setBaseNode(nodeId);
            Node fromNode = graph.getNode(String.valueOf(nodeId));

//            log.info("================================ INSIDE FOR LOOP ============================================");
            while (iter.next()) {
                int toId = iter.getAdjNode();
                Node toNode = graph.getNode(String.valueOf(toId));
                if (toNode != null) {
                    double distance = iter.getDistance();
                    graph.addEdge(fromNode, toNode, distance);
                }
            }
        }
        log.info("================================ COMPLETED BUILDING CUSTOM GRAPH =================================");

        return graph;
    }

    // Helper: find closest node to given lat/lon
    private Node getClosestNode(double lat, double lon) {
        Node closest = null;
        double minDist = Double.MAX_VALUE;
        for (Node node : customGraph.getNodes()) {
            double d = haversine(lat, lon, node.getLat(), node.getLon());
            if (d < minDist) {
                minDist = d;
                closest = node;
            }
        }
        return closest;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public PathResponse calculatePath(PathRequest request) {
        Node startNode = getClosestNode(request.getStartLat(), request.getStartLon());
        Node endNode = getClosestNode(request.getEndLat(), request.getEndLon());

        PathResult result;

        switch (request.getAlgorithm().toLowerCase()) {
            case "astar":
                result = AStarAlgorithm.findPath(customGraph, startNode, endNode);
                break;
            case "dijkstra":
                result = DijkstraAlgorithm.findPath(customGraph, startNode, endNode);
                break;
            case "bellman-ford":
                result = BellmanFordAlgorithm.findPath(customGraph, startNode, endNode);
                break;
            case "best":
                result = BestFirstSearchAlgorithm.findPath(customGraph, startNode, endNode);
                break;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + request.getAlgorithm());
        }

        return toPathResponse(result);
    }

    private PathResponse toPathResponse(PathResult result) {
        List<double[]> finalPath = new ArrayList<>();
        for (Node node : result.getPath()) {
            finalPath.add(node.toCoordinates());
        }

        List<double[]> exploration = new ArrayList<>();
        for (Node node : result.getVisitedOrder()) {
            exploration.add(node.toCoordinates());
        }

        return new PathResponse(
                finalPath,
                exploration,
                result.getTotalDistance(),
                result.getVisitedOrder().size(),
                finalPath.size()
        );
    }

}