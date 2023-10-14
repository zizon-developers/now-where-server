package com.spring.nowwhere.api.v1.redis.geo;

import com.spring.nowwhere.api.IntegrationTestSupport;
import com.spring.nowwhere.api.v1.entity.bet.*;
import com.spring.nowwhere.api.v1.entity.bet.exception.BetNotFoundException;
import com.spring.nowwhere.api.v1.entity.bet.geo.BetGeoService;
import com.spring.nowwhere.api.v1.entity.bet.geo.dto.BetRefreshDto;
import com.spring.nowwhere.api.v1.entity.bet.geo.dto.ResponseBetGeo;
import com.spring.nowwhere.api.v1.entity.bet.repository.BetRepository;
import com.spring.nowwhere.api.v1.entity.user.User;
import com.spring.nowwhere.api.v1.entity.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class BetGeoServiceTest extends IntegrationTestSupport {
    @Autowired
    private GeoOperations<String, String> geoOperations;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private BetGeoService betGeoService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BetRepository betRepository;

    @AfterEach
    void tearDown(){
        ScanOptions scanOptions = ScanOptions.scanOptions()
                                        .match("*").count(3).build();
        redisTemplate.scan(scanOptions).stream()
                        .forEach(redisTemplate::delete);
    }
    @Test
    @DisplayName("사용자 내기가 진행중이라면 내기를 새로고침 할 수 있다.")
    public void refreshBet() {
        // given
        User bettor = createUserAndSave("bettor1");
        User receiver = createUserAndSave("receiver1");

        int amount = 4500;
        Location location = new Location(37.557529,126.924404,"홍대입구역");
        LocalDateTime startTime = LocalDateTime.of(2021, 2, 3, 1, 10);
        LocalDateTime endTime = LocalDateTime.of(2021, 2, 5, 1, 20);

        BetDateTime betDateTime = new BetDateTime(startTime, endTime);
        BetInfo betInfo = createBetInfo(amount, betDateTime,location);
        createBetAndSave(bettor, receiver, betInfo, BetStatus.IN_PROGRESS);
        // when
        BetRefreshDto betRefreshDto = new BetRefreshDto(location,betDateTime);
        betGeoService.refreshBet(betRefreshDto,bettor.getCheckId());
        // then
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(String.format("%s:%s",bettor.getCheckId(),location.getLocationName())).count(1).build();

        assertThat(redisTemplate.scan(scanOptions).hasNext()).isTrue();

    }

    @Test
    @DisplayName("사용자의 위도 경도를 통해서 목적지와 거리차이를 알 수 있다.")
    public void nearByDestination() {
        // given
        Location userLocation = new Location(37.557453,126.924288,"user");
        Location destLocation = new Location(37.557529,126.924404,"홍대입구역");
        String key = String.format("%s:%s", userLocation.getLocationName(), destLocation.getLocationName());
        geoOperations.add(key, new Point(destLocation.getLongitude(), destLocation.getLatitude()),
                                                                            destLocation.getLocationName());
        // when
        ResponseBetGeo responseBetGeo = betGeoService.nearByDestination(userLocation, destLocation.getLocationName());
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
        String key = String.format("%s:%s", userLocation.getLocationName(), destLocation.getLocationName());
        // when // then
        assertThatThrownBy(() -> betGeoService.nearByDestination(userLocation, destLocation.getLocationName()))
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
    private Bet createBetAndSave(User bettor, User receiver, BetInfo betInfo, BetStatus betStatus) {
        Bet bet = Bet.builder()
                .bettor(bettor)
                .receiver(receiver)
                .betStatus(betStatus)
                .betInfo(betInfo)
                .build();
        return betRepository.save(bet);
    }
    private BetInfo createBetInfo(int amount, BetDateTime betDateTime, Location location) {
        return BetInfo.builder()
                .amount(amount)
                .betDateTime(betDateTime)
                .appointmentLocation(location)
                .build();
    }
    private User createUserAndSave(String name) {
        User user = User.builder()
                .checkId(name+"id")
                .email(name+"@test.com")
                .profileImg(name+"img")
                .name(name)
                .build();
        return userRepository.save(user);
    }

}