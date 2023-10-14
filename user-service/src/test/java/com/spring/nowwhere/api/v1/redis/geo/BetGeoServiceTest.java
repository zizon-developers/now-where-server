package com.spring.nowwhere.api.v1.redis.geo;

import com.spring.nowwhere.api.IntegrationTestSupport;
import com.spring.nowwhere.api.v1.entity.bet.Location;
import com.spring.nowwhere.api.v1.entity.bet.exception.BetNotFoundException;
import com.spring.nowwhere.api.v1.entity.bet.geo.BetGeoService;
import com.spring.nowwhere.api.v1.entity.bet.geo.dto.ResponseBetGeo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import static org.assertj.core.api.Assertions.*;

class BetGeoServiceTest extends IntegrationTestSupport {
    @Autowired
    private GeoOperations<String, String> geoOperations;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private BetGeoService betGeoService;

    @AfterEach
    void tearDown(){
        ScanOptions scanOptions = ScanOptions.scanOptions()
                                        .match("*").count(3).build();
        redisTemplate.scan(scanOptions).stream()
                        .forEach(redisTemplate::delete);
    }

    @Test
    @DisplayName("사용자의 위도 경도를 통해서 목적지와 거리차이를 알 수 있다.")
    public void nearByDestination() {
        // given
        Location userLocation = new Location(37.557453,126.924288,"user");
        Location destLocation = new Location(37.557529,126.924404,"홍대입구역");
        String key = String.format("%s:%s", userLocation.getName(), destLocation.getName());
        geoOperations.add(key, new Point(destLocation.getLongitude(), destLocation.getLatitude()),
                                                                            destLocation.getName());
        // when
        ResponseBetGeo responseBetGeo = betGeoService.nearByDestination(userLocation, destLocation.getName());
        // then
        double distance = calculateDistance(userLocation.getLatitude(),
                userLocation.getLongitude(), destLocation.getLatitude(), destLocation.getLongitude());
        assertThat(responseBetGeo.getUserId()).isEqualTo("user");
        assertThat(String.format("%.2f",responseBetGeo.getDistance())).isEqualTo(String.format("%.2f",distance));
    }
    @Test
    @DisplayName("key가 없는 경우 예외가 발생한다.")
    public void nearByDestination_EX() {
        // given
        Location userLocation = new Location(37.557453,126.924288,"user");
        Location destLocation = new Location(37.557529,126.924404,"홍대입구역");
        String key = String.format("%s:%s", userLocation.getName(), destLocation.getName());
        // when // then
        assertThatThrownBy(() -> betGeoService.nearByDestination(userLocation, destLocation.getName()))
                .isInstanceOf(BetNotFoundException.class)
                .hasMessage("진행중인 내기를 업데이트 해주세요");
    }
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반지름 (km)

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

}