package com.wcc.geographicdistance.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.wcc.geographicdistance.domain.DistanceBetweenLocations;
import com.wcc.geographicdistance.domain.Postcode;
import com.wcc.geographicdistance.repo.PostcodeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostcodeServiceImpl implements PostcodeService{
    private final PostcodeRepo postcodeRepo;
    private final static double EARTH_RADIUS = 6371; // radius in kilometers

    @Override
    public Postcode getPostcode(String postcode) {
        return postcodeRepo.findByPostcode(postcode);
    }

    @Override
    public List<Postcode> getPostcodes() {
        return postcodeRepo.findAll();
    }

    @Override
    public Postcode savePostcode(Postcode postcode) {
        log.info("Saving new postcode {} to the database", postcode.getPostcode());
        return postcodeRepo.save(postcode);
    }

    @Override
    public List<Postcode> saveAll(List<Postcode> postcodes) {
        log.info("Saving {} postcodes to the database", postcodes.size());
        postcodeRepo.saveAll(postcodes);
        return postcodeRepo.findAll();
    }

    @Override
    public DistanceBetweenLocations calculateDistanceBetweenPostcodes(String postcodeA, String postcodeB) {
        log.info("Calculating distance between postcodeA {} and postcodeB {}",
                postcodeA,postcodeB);

        Postcode locationA = this.getPostcode(postcodeA);
        Postcode locationB = this.getPostcode(postcodeB);

        if (locationA == null || locationB == null ){
            //Check postcodes form CSV
            List<Postcode> postcodes = csvToPostcodeList(false);
            List<Postcode> postcodesToSave;

            postcodesToSave = postcodes
                    .stream()
                    .filter(postcode ->
                            postcode.getPostcode().equalsIgnoreCase(postcodeA) ||
                            postcode.getPostcode().equalsIgnoreCase(postcodeB))
                    .collect(toList());

            if (postcodesToSave.size() == 0){
                throw new NoSuchElementException(String.format("The postcodes do not exist in the CSV file"));
            }else if (postcodesToSave.size() == 1){
                Postcode foundPostcode = postcodesToSave.get(0);
                throw new NoSuchElementException(String.format("The postcode %s do not exist in the CSV file",
                        foundPostcode.getPostcode().equalsIgnoreCase(postcodeA)?postcodeB:postcodeA));
            }else{
                locationA = postcodesToSave.get(0);
                locationB = postcodesToSave.get(1);
                this.saveAll(postcodesToSave);
            }
        }

        DistanceBetweenLocations distance = new DistanceBetweenLocations();

        distance.setLocationA(locationA);
        distance.setLocationB(locationB);
        distance.setDistanceBetweenLocations(
                this.calculateDistance(
                        locationA.getLatitude(),
                        locationA.getLongitude(),
                        locationB.getLatitude(),
                        locationB.getLongitude())
        );

        return distance;
    }

    public void load100FirstPostcodesFromCSV(){
        List<Postcode> postcodes = csvToPostcodeList(true).subList(0,99);
        this.saveAll(postcodes);
    }

    private List<Postcode> csvToPostcodeList(Boolean first100records) {
        //Check postcodes form CSV and added to DB if wasn't added before
        String source = System.getProperty("user.dir")+"/ukpostcodes.zip";
        String destination = System.getProperty("user.dir");
        String csvFile = destination +(first100records?"/ukpostcodesfirst100.csv":"/ukpostcodes.csv");
        List<Postcode> postcodes;
        //Unzip file
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            throw new RuntimeException(String.format("Error unzipping file:%s",e.getMessage()));
        }
        //Convert CSV to List
        try {
            postcodes = new CsvToBeanBuilder(new FileReader(csvFile))
                    .withType(Postcode.class)
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error converting CSV to List: %s",e.getMessage()));
        }
        return postcodes;
    }

    private double calculateDistance(double latitude, double longitude, double latitude2, double
            longitude2) {
        // Using Haversine formula! See Wikipedia;
        double lon1Radians = Math.toRadians(longitude);
        double lon2Radians = Math.toRadians(longitude2);
        double lat1Radians = Math.toRadians(latitude);
        double lat2Radians = Math.toRadians(latitude2);
        double a = haversine(lat1Radians, lat2Radians)
                + Math.cos(lat1Radians) * Math.cos(lat2Radians) * haversine(lon1Radians, lon2Radians);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (EARTH_RADIUS * c);
    }

    private double haversine(double deg1, double deg2) {
        return square(Math.sin((deg1 - deg2) / 2.0));
    }
    private double square(double x) {
        return x * x;
    }
}
