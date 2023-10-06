package com.spring.nowwhere.api.v1.redis.geo;

import com.spring.nowwhere.api.v1.redis.geo.dto.RequestBetGeo;
import com.spring.nowwhere.api.v1.redis.geo.dto.ResponseBetGeo;
import com.spring.nowwhere.api.v1.security.jwt.JwtProperties;
import com.spring.nowwhere.api.v1.security.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LocationController {
    private final GeoService geoService;
    private final GeoOperations<String, String> geoOperations;
    private final TokenProvider tokenProvider;


    @PostMapping("bets/location")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "내기를 진행한 실시간 사용자 위치 정보", description = "목적지와 사용자 거리의 차이를 알 수 있다.")
    public ResponseEntity<ResponseBetGeo> locations(@RequestBody RequestBetGeo requestBetGeo,
                                                    HttpServletRequest request) {

        String checkId = getCheckIdByRequest(request);
        Location location = new Location(checkId, requestBetGeo.getLatitude(), requestBetGeo.getLongitude());
        ResponseBetGeo responseBetGeo = geoService.nearByDestination(location, requestBetGeo.getDestination());
        return ResponseEntity.ok(responseBetGeo);
    }
    @PostMapping("/location")
    @Operation(summary = "실시간 사용자 위치 정보 저장", description = "실시간 사용자 위치 정보 저장")
    public ResponseEntity<String> addLocation(@RequestBody Location location,String key) {
        Point point = new Point(location.getLongitude(), location.getLatitude());
        geoOperations.add(key, point, location.getName());
        geoService.add(location,key);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/location/nearby")
    @Operation(summary = "실시간 사용자 반경 정보 조회", description = "특정 좌표와 키를 통해서 반경 몇 키로 미터에 요소를 조회할 수 있다.")
    public ResponseEntity<List<String>> locations(Double longitude, Double latitude, Double km, String key) {

        List<String> locations = geoService.nearByVenues(longitude,latitude,km,key);
        return ResponseEntity.ok(locations);
    }

    private String getCheckIdByRequest(HttpServletRequest request) {
        String token = request.getHeader(JwtProperties.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");

        return tokenProvider.getCheckIdFromAccessToken(token);
    }
}
