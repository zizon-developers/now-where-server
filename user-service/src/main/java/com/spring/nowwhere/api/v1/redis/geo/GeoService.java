package com.spring.nowwhere.api.v1.redis.geo;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GeoService {
    public static final String VENUS_VISITED = "venues_visited";
    private final GeoOperations<String, String> geoOperations;
    private final RedisTemplate<String, String> redisTemplate;


    public GeoService(GeoOperations<String, String> geoOperations, RedisTemplate<String, String> redisTemplate) {
        this.geoOperations = geoOperations;
        this.redisTemplate = redisTemplate;
    }

    public void add(Location location) {
        Point point = new Point(location.getLongitude(), location.getLatitude());
        geoOperations.add(VENUS_VISITED, point, location.getName());
    }

    public List<String> nearByVenues(Location location, Double kmDistance, String destination) {
        //우리는 반경 15m로 할꺼면 kmDistance 수정해야된다 m단위로
        // 계속해서 add 해서 최신위치를 갱신해줘야 한다.

        Circle circle = new Circle(new Point(location.getLongitude(), location.getLatitude()), new Distance(kmDistance, Metrics.KILOMETERS));


        GeoResults<RedisGeoCommands.GeoLocation<String>> res = geoOperations.radius(VENUS_VISITED, circle);

        //도착지가 있거나 없을 수 있다. (자신의 위치 정보로 반경을 조회하니까 내 자신이 나오는 일은 없음)
        Optional<String> find = res.getContent().stream()
                .map(GeoResult::getContent)
                .map(GeoLocation::getName)
                .filter(name -> name.equals(destination))
                .findFirst();


        Distance distance = geoOperations.distance(VENUS_VISITED, destination, location.getName(), Metrics.KILOMETERS);


        System.out.println("distance = " + distance);
        Set<String> keyName = redisTemplate.keys(VENUS_VISITED);
        for (String s : keyName) {
            System.out.println("key="+s); //다음에 유틸클래스로 빼서 내기 목적지 이름 사용자a이름 b이름 이런식으로 해서 : <- 레디스 네이밍 컨벤션 지켜서 유니크한 key만들어주자
        }
        ScanOptions scanOptions = ScanOptions.scanOptions().match("keyName").count(10).build();
        Cursor<String> scan = redisTemplate.scan(scanOptions);

        while(scan.hasNext()){
            String next = scan.next();
            System.out.println("scan="+next);
        }

        //distance = 155023.3332 METERS

        return res.getContent().stream()
                .map(GeoResult::getContent)
                .map(GeoLocation::getName)
                .collect(Collectors.toList());
    }
}
