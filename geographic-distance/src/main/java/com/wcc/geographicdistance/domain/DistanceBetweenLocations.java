package com.wcc.geographicdistance.domain;

import lombok.Data;

@Data
public class DistanceBetweenLocations {
    private Postcode locationA;
    private Postcode locationB;
    private Double distanceBetweenLocations;
    private String unit = "km";
}
