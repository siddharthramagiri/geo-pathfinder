package com.map.pathfinder.service;

import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
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

    public static void main_importOnly() { }

    @PostConstruct
    public void init() {
        log.info("OSM file path: {}", osmFilePath);
        log.info("Graph cache path: {}", graphCachePath);
        GraphHopperConfig config = new GraphHopperConfig();
        config.putObject("graph.dataaccess", "MMAP");
        config.putObject("datareader.file", osmFilePath);
        config.putObject("graph.location", graphCachePath);

        config.putObject("import.osm.ignored_highways", "elevator,bus_guideway,raceway,proposed,planned,abandoned,platform,construction");

        Profile profile = new Profile("car")
                .setVehicle("car")
                .setWeighting("custom")
                .setCustomModel(new CustomModel());

        config.setProfiles(List.of(profile));
        config.setCHProfiles(List.of());
        config.setLMProfiles(List.of());

        hopper = new GraphHopper();
        hopper.init(config);

        hopper.setOSMFile(osmFilePath);
        hopper.setGraphHopperLocation(graphCachePath);

        boolean loaded = hopper.load();
        if (!loaded) {
            throw new IllegalStateException("Graph cache not found at: " + graphCachePath +
                        ". Rebuild the Docker image to regenerate the graph cache.");
        }

        this.graph = hopper.getBaseGraph();
        this.nodeAccess = graph.getNodeAccess();
        this.locationIndex = hopper.getLocationIndex();

        if (locationIndex == null) {
            throw new IllegalStateException("LocationIndex not initialized");
        }

        log.info("GraphHopper loaded | Nodes={}", graph.getNodes());
    }

    public PathResponse calculatePath(PathRequest request) {
        int startNode = findClosestNode(
                request.getStartLat(), request.getStartLon()
        );
        int endNode = findClosestNode(
                request.getEndLat(), request.getEndLon()
        );

        Algorithm algorithm = AlgorithmFactory.getAlgorithm(request.getAlgorithm());
        PathResult result = algorithm.find(graph, startNode, endNode);

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
