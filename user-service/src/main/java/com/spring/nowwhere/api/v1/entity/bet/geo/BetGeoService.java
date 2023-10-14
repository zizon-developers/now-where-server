package com.spring.nowwhere.api.v1.entity.bet.geo;

import com.spring.nowwhere.api.v1.entity.bet.Location;
import com.spring.nowwhere.api.v1.entity.bet.exception.BetNotFoundException;
import com.spring.nowwhere.api.v1.entity.bet.geo.dto.BetRefreshDto;
import com.spring.nowwhere.api.v1.entity.bet.geo.dto.ResponseBetGeo;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.domain.geo.GeoLocation;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BetGeoService {
    private final GeoOperations<String, String> geoOperations;
    private final RedisTemplate<String, String> redisTemplate;
    private final BetRepository betRepository;
    private final UserRepository userRepository;

    public ResponseBetGeo nearByDestination(Location location, String destination){
        String key = getFormat(location, destination);
        ScanOptions scanOptions = ScanOptions.scanOptions().match(key).count(1).build();

        boolean isBet = redisTemplate.scan(scanOptions).hasNext();
        if(!isBet) throw new BetNotFoundException("진행중인 내기를 업데이트 해주세요");

        //사용자 위치정보 저장
        Point point = new Point(location.getLongitude(), location.getLatitude());
        geoOperations.add(key, point, location.getLocationName());

        //도착지와 사용자의 거리차이 km 단위
        Distance distance = geoOperations.distance(key, destination, location.getLocationName(), Metrics.KILOMETERS);
        return new ResponseBetGeo(distance.getValue(), location.getLocationName());
    }

    public void add(Location location, String key) {
        Point point = new Point(location.getLongitude(), location.getLatitude());
        geoOperations.add(key, point, location.getLocationName());
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
        return String.format("%s:%s", location.getLocationName(), destination);
    }

    public void refreshBet(BetRefreshDto betRefreshDto, String checkId) {

        Location destinationInfo = betRefreshDto.getDestinationInfo();

        User user = userRepository.findByCheckId(checkId)
                .orElseThrow(() -> new UsernameNotFoundException("특정 유저가 존재하지 않습니다."));
        betRepository.findTimeRangeAndDestination(user, betRefreshDto.getBetDateTime(),
                        destinationInfo.getLocationName())
                .orElseThrow(() -> new BetNotFoundException("내기가 존재하지 않습니다."));

        //redis
        String userKey = redisNameFormat(checkId, destinationInfo.getLocationName());
        geoOperations.add(userKey, new Point(destinationInfo.getLongitude(),
                                             destinationInfo.getLatitude()), destinationInfo.getLocationName());
    }
    private String redisNameFormat(String user, String destination){
        return String.format("%s:%s", user, destination);
    }
}
