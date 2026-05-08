package com.map.pathfinder.algorithm;

import com.graphhopper.storage.BaseGraph;
import com.map.pathfinder.dto.PathResult;

public interface Algorithm {
    PathResult find(BaseGraph graph, int start, int end);
}
