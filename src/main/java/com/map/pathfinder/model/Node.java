package com.map.pathfinder.model;

import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class Node {
    private String id;
    private double lat;
    private double lon;

    public double[] toCoordinates() {
        return new double[]{lat, lon};
    }
}
