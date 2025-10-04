package com.map.pathfinder.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Coordinate {
    private double lat;
    private double lon;

    public double[] toArray() {
        return new double[]{lat, lon};
    }
}