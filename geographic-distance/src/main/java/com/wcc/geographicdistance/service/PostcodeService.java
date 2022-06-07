package com.wcc.geographicdistance.service;

import com.wcc.geographicdistance.domain.DistanceBetweenLocations;
import com.wcc.geographicdistance.domain.Postcode;

import java.util.List;

public interface PostcodeService {
    Postcode getPostcode(String postcode);
    List<Postcode> getPostcodes();
    Postcode savePostcode(Postcode postcode);
    List<Postcode> saveAll(List<Postcode> postcodes);
    void load100FirstPostcodesFromCSV();
    DistanceBetweenLocations calculateDistanceBetweenPostcodes(String postcodeA, String postcodeB);
}
