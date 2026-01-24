package com.map.pathfinder.service;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.storage.BaseGraph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.Snap;
import com.graphhopper.util.CustomModel;
import com.map.pathfinder.algorithm.*;
import com.map.pathfinder.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.*;


@Slf4j
@Service
public class PathfindingService {

    private GraphHopper hopper;
    private BaseGraph graph;
    private NodeAccess nodeAccess;
    private LocationIndex locationIndex;


    @Value("${graphhopper.osm-file}")
    private String osmFilePath;
    @Value("${graphhopper.graph-cache}")
    private String graphCachePath;

    @PostConstruct
    public void init() {

        hopper = new GraphHopper();

        hopper.setOSMFile(osmFilePath);
        hopper.setGraphHopperLocation(graphCachePath);

        CustomModel customModel = new CustomModel();

        Profile profile = new Profile("car")
                .setVehicle("car")
                .setWeighting("custom")
                .setCustomModel(customModel);

        hopper.setProfiles(profile);

        // Disable CH for simplicity (recommended for custom algorithms)
        hopper.getCHPreparationHandler().setCHProfiles();

        boolean loaded = hopper.load();
        if (!loaded) {
            log.info("Graph cache not found or incompatible. Importing OSM...");
            hopper.importOrLoad();
        }

        this.graph = hopper.getBaseGraph();
        this.nodeAccess = graph.getNodeAccess();
        this.locationIndex = hopper.getLocationIndex();

        if (locationIndex == null) {
            throw new IllegalStateException("LocationIndex not initialized");
        }

        log.info("GraphHopper initialized successfully | Nodes={}", graph.getNodes());
    }

    public PathResponse calculatePath(PathRequest request) {

        int startNode = findClosestNode(
                request.getStartLat(), request.getStartLon()
        );
        int endNode = findClosestNode(
                request.getEndLat(), request.getEndLon()
        );

        PathResult result;

        switch (request.getAlgorithm().toLowerCase()) {
            case "astar":
                result = AStarAlgorithm.find(graph, startNode, endNode);
                break;
            case "dijkstra":
                result = DijkstraAlgorithm.find(graph, startNode, endNode);
                break;
            case "best":
                result = BestFirstSearchAlgorithm.find(graph, startNode, endNode);
                break;
            default:
                throw new IllegalArgumentException("Unknown algorithm");
        }

        return toResponse(result);
    }

    private int findClosestNode(double lat, double lon) {
        Snap qr = locationIndex.findClosest(
                lat, lon, EdgeFilter.ALL_EDGES
        );
        if (!qr.isValid()) {
            throw new IllegalStateException("No nearby node found");
        }
        return qr.getClosestNode();
    }

    private PathResponse toResponse(PathResult result) {

        List<double[]> finalPath = new ArrayList<>();
        for (int nodeId : result.path()) {
            finalPath.add(new double[]{
                    nodeAccess.getLat(nodeId),
                    nodeAccess.getLon(nodeId)
            });
        }

        List<double[]> explored = new ArrayList<>();
        for (int nodeId : result.visited()) {
            explored.add(new double[]{
                    nodeAccess.getLat(nodeId),
                    nodeAccess.getLon(nodeId)
            });
        }

        return new PathResponse(
                finalPath,
                explored,
                result.distance(),
                result.visited().size(),
                result.path().size()
        );
    }
}
