package com.spring.nowwhere.api.v1.redis.geo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class LocationController {
    private final GeoService geoService;
    private final GeoOperations<String, String> geoOperations;


    public LocationController(GeoService geoService, GeoOperations<String, String> geoOperations) {
        this.geoService = geoService;
        this.geoOperations = geoOperations;
    }

    @PostMapping("/location")
    public ResponseEntity<String> addLocation(@RequestBody Location location) {
        log.info("getLatitude={},getLongitude={}", location.getLatitude(), location.getLongitude());
        Point point = new Point(location.getLongitude(), location.getLatitude());
        geoOperations.add("keyName", point, location.getName());


        geoService.add(location);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/location/nearby")
    public ResponseEntity<List<String>> locations(Double longitude, Double latitude, Double km, String destination, HttpServletRequest request) {
        String header = request.getHeader("test");
        Location location1 = new Location(header, latitude, longitude);
        List<String> locations = geoService.nearByVenues(location1, km , destination);


        for (String location : locations) {
            System.out.println(location);
        }
        return ResponseEntity.ok(locations);
    }
}
