package com.spring.nowwhere.api.v1.redis.geo;

import com.spring.nowwhere.api.v1.entity.bet.exception.BetNotFoundException;
import com.spring.nowwhere.api.v1.redis.geo.dto.ResponseBetGeo;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeoService {
    private final GeoOperations<String, String> geoOperations;
    private final RedisTemplate<String, String> redisTemplate;

    public GeoService(GeoOperations<String, String> geoOperations, RedisTemplate<String, String> redisTemplate) {
        this.geoOperations = geoOperations;
        this.redisTemplate = redisTemplate;
    }

    public ResponseBetGeo nearByDestination(Location location, String destination){
        String key = getFormat(location, destination);
        ScanOptions scanOptions = ScanOptions.scanOptions().match(key).count(1).build();

        boolean isBet = redisTemplate.scan(scanOptions).hasNext();
        if(!isBet) throw new BetNotFoundException("진행중인 내기가 없습니다.");

        //사용자 위치정보 저장
        Point point = new Point(location.getLongitude(), location.getLatitude());
        geoOperations.add(key, point, location.getName());

        //도착지와 사용자의 거리차이 km 단위
        Distance distance = geoOperations.distance(key, destination, location.getName(), Metrics.KILOMETERS);
        return new ResponseBetGeo(distance.getValue(), location.getName());
    }

    public void add(Location location, String key) {
        Point point = new Point(location.getLongitude(), location.getLatitude());
        geoOperations.add(key, point, location.getName());
    }

    public List<String> nearByVenues(Double longitude, Double latitude, Double kmDistance, String key) {

        Circle circle = new Circle(new Point(longitude, latitude), new Distance(kmDistance, Metrics.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<String>> res = geoOperations.radius(key, circle);

        return res.getContent().stream()
                .map(GeoResult::getContent)
                .map(GeoLocation::getName)
                .collect(Collectors.toList());
    }
    private static String getFormat(Location location, String destination) {
        return String.format("%s:%s", location.getName(), destination);
    }
}
