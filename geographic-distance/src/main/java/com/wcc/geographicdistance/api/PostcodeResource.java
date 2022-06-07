package com.wcc.geographicdistance.api;

import com.wcc.geographicdistance.domain.DistanceBetweenLocations;
import com.wcc.geographicdistance.domain.Postcode;
import com.wcc.geographicdistance.service.PostcodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/geo-distance")
@RequiredArgsConstructor
public class PostcodeResource {
    private final PostcodeService postcodeService;

    @GetMapping("/postcodes")
    public ResponseEntity<List<Postcode>> getPostcodes(){
        return ResponseEntity.ok().body(postcodeService.getPostcodes());
    }

    @PostMapping("/postcodes/save")
    public ResponseEntity<List<Postcode>> savePostcodes(@RequestBody List<Postcode> postcodes){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/geo-distance/postcodes/save").toUriString());
        return ResponseEntity.created(uri).body(postcodeService.saveAll(postcodes));
    }

    @PostMapping("/postcode/save")
    public ResponseEntity<Postcode> savePostcode(@RequestBody Postcode postcode){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/geo-distance/postcode/save").toUriString());
        return ResponseEntity.created(uri).body(postcodeService.savePostcode(postcode));
    }

    @GetMapping("/postcodes/distance")
    public ResponseEntity<DistanceBetweenLocations> getDistanceBetweenPostcodes(
            @RequestBody Map<String, String> requestMap){

        String postcodeA = requestMap.get("postcodeA");
        String postcodeB = requestMap.get("postcodeB");

        DistanceBetweenLocations distance = postcodeService.calculateDistanceBetweenPostcodes(postcodeA, postcodeB);
        return ResponseEntity.ok().body(distance);
    }
}
