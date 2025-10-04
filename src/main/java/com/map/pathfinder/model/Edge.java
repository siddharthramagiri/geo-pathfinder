package com.map.pathfinder.model;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Edge {
    private Node target;
    private double weight; // distance in meters
}